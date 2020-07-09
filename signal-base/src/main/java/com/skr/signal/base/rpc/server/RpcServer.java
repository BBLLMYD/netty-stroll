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
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author mqw
 * @create 2020-06-22-12:33
 */
@Slf4j
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

    public static RpcServer run() {
        if (serverInstance == null) {
            synchronized (RpcServer.class) {
                if (serverInstance == null) {
                    String serverAddress;
                    PropertiesUtil propertiesUtil = PropertiesUtil.newInstance("config-rpc.properties");
                    String port = propertiesUtil.readProperty("server.address");
                    String registrationAddress = propertiesUtil.readProperty("registration.address");
                    if (StringUtils.isNotEmpty(port) && StringUtils.isNotEmpty(registrationAddress)) {
                        ServiceRegistry serviceRegistry;
                        String serviceRegistryImplCfg = propertiesUtil.readProperty("serviceRegistry.impl");
                        try {
                            if (StringUtils.isEmpty(serviceRegistryImplCfg)) {
                                serviceRegistry = new ZKServiceRegistry();
                            } else {
                                serviceRegistry = (ServiceRegistry) Class.forName(serviceRegistryImplCfg).newInstance();
                            }
                            serviceRegistry.registryAddress(registrationAddress);
                            String address = InetAddress.getLocalHost().getHostAddress();
                            serverAddress = address + ":" + port;
                        } catch (Exception e) {
                            throw new RuntimeException("初始化serviceRegistry期间异常", e);
                        }
                        serverInstance = new RpcServer(serverAddress, serviceRegistry);
                    }
                    serverInstance.shutDownHook();
                    serverInstance.start();
                }
            }
        }
        return serverInstance;
    }

    private void shutDownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(CuratorUtils::clearRegistry));
    }


    private void start() {
        bossGroup = new NioEventLoopGroup();
        workerGroup = new NioEventLoopGroup();
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(bossGroup, workerGroup);
        bootstrap.channel(NioServerSocketChannel.class);
        bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) {
                ChannelPipeline pipeline = ch.pipeline();
                pipeline.addLast(new IdleStateHandler(30, 0, 0, TimeUnit.SECONDS));
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

        if (serviceRegistry != null) {
            PropertiesUtil propertiesUtil = PropertiesUtil.newInstance("config-rpc.properties");
            String basePackage = propertiesUtil.readProperty("server.basePackage");
            List<Class<?>> list = ClassUtil.getClassList(basePackage, true);
            if (CollectionUtils.isNotEmpty(list)) {
                list = list.stream().filter(vo -> vo.isAnnotationPresent(RpcServiceTag.class)).collect(Collectors.toList());
                list.stream().forEach(vo -> {
                    String serviceName = vo.getAnnotation(RpcServiceTag.class).targetService().getName();
                    serviceRegistry.register(serviceName, serverAddress);
                    try {
                        handlerMap.put(serviceName, vo.newInstance());
                    } catch (Exception e) {
                        log.error("初始化业务实现类异常", e);
                    }
                });
            }
        }
        try {
            ChannelFuture future = bootstrap.bind(host, port).sync();
            future.channel().closeFuture().sync();
        } catch (Exception e) {
            throw new RuntimeException("服务启动失败", e);
        }
    }

}
