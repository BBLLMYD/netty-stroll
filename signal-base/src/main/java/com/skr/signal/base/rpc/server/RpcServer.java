package com.skr.signal.base.rpc.server;

import com.skr.signal.base.registry.ServiceRegistry;
import com.skr.signal.base.registry.impl.zookeeper.CuratorUtils;
import com.skr.signal.base.registry.impl.zookeeper.ZKServiceRegistry;
import com.skr.signal.base.rpc.letter.RpcDecoder;
import com.skr.signal.base.rpc.letter.RpcEncoder;
import com.skr.signal.base.rpc.letter.RpcRequest;
import com.skr.signal.base.rpc.letter.RpcResponse;
import com.skr.signal.base.rpc.server.anno.RpcServiceTag;
import com.skr.signal.common.util.ClassUtil;
import com.skr.signal.common.util.PropertiesUtil;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author mqw
 * @create 2020-06-22-12:33
 */
public class RpcServer {

    private String serverAddress;
    private Map<String, Object> handlerMap = new HashMap<>();

    private ServiceRegistry serviceRegistry;

    private EventLoopGroup bossGroup = null;
    private EventLoopGroup workerGroup = null;

    private RpcServer(String serverAddress, ServiceRegistry serviceRegistry) {
        this.serverAddress = serverAddress;
        this.serviceRegistry = serviceRegistry;
    }

    private static volatile RpcServer serverInstance;

    public static RpcServer getInstance() throws InterruptedException {
        if(serverInstance == null){
            synchronized (RpcServer.class){
                if(serverInstance == null){
                    PropertiesUtil propertiesUtil = PropertiesUtil.newInstance("config-rpc.properties");
                    String serverAddress = propertiesUtil.readProperty("server.address");
                    String registrationAddress = propertiesUtil.readProperty("registration.address");
                    if(StringUtils.isNotEmpty(serverAddress) && StringUtils.isNotEmpty(registrationAddress)){
                        ServiceRegistry serviceRegistry = new ZKServiceRegistry(registrationAddress);
                        serverInstance = new RpcServer(serverAddress,serviceRegistry);
                    }
                    serverInstance.shutDownHook();
                    serverInstance.start();
                }
            }
        }
        return serverInstance;
    }

    private void shutDownHook(){
        Runtime.getRuntime().addShutdownHook(new Thread(CuratorUtils::clearRegistry));
    }


    private void start() throws InterruptedException {
        bossGroup = new NioEventLoopGroup();
        workerGroup = new NioEventLoopGroup();
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(bossGroup, workerGroup);
        bootstrap.channel(NioServerSocketChannel.class);
        bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) {
                ChannelPipeline pipeline = ch.pipeline();
                pipeline.addLast(new LengthFieldBasedFrameDecoder(65536, 0, 4, 0, 0));
                pipeline.addLast(new RpcDecoder(RpcRequest.class));
                pipeline.addLast(new RpcEncoder(RpcResponse.class));
                pipeline.addLast(new RpcHandler(handlerMap));
            }
        });
        bootstrap.option(ChannelOption.SO_BACKLOG, 128);
        bootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);

        String[] array = serverAddress.split(":");
        String host = array[0];
        int port = Integer.parseInt(array[1]);

        ChannelFuture future = bootstrap.bind(host, port).sync();
        if (serviceRegistry != null) {
            PropertiesUtil propertiesUtil = PropertiesUtil.newInstance("config-rpc.properties");
            String basePackage = propertiesUtil.readProperty("server.basePackage");
            List<Class<?>> list = ClassUtil.getClassList(basePackage, true);
            if(CollectionUtils.isNotEmpty(list)){
                list = list.stream().filter(vo->vo.isAnnotationPresent(RpcServiceTag.class)).collect(Collectors.toList());
                list.stream().forEach(vo->{
                    String serviceName = vo.getAnnotation(RpcServiceTag.class).serviceName();
                    serviceRegistry.register(serviceName,serverAddress);
                });
            }
        }
        future.channel().closeFuture().sync();
    }



}
