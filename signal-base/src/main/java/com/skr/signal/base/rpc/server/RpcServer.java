package com.skr.signal.base.rpc.server;

import com.skr.signal.base.registey.ServiceRegistry;
import com.skr.signal.base.rpc.letter.RpcDecoder;
import com.skr.signal.base.rpc.letter.RpcEncoder;
import com.skr.signal.base.rpc.letter.RpcRequest;
import com.skr.signal.base.rpc.letter.RpcResponse;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

import java.util.HashMap;
import java.util.Map;

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

    public RpcServer(String serverAddress, ServiceRegistry serviceRegistry) {
        this.serverAddress = serverAddress;
        this.serviceRegistry = serviceRegistry;
    }

    public void start() throws InterruptedException {
        bossGroup = new NioEventLoopGroup();
        workerGroup = new NioEventLoopGroup();
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) {
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast(new LengthFieldBasedFrameDecoder(65536, 0, 4, 0, 0));
                        pipeline.addLast(new RpcDecoder(RpcRequest.class));
                        pipeline.addLast(new RpcEncoder(RpcResponse.class));
                        pipeline.addLast(new RpcHandler(handlerMap));
                    }
                }).option(ChannelOption.SO_BACKLOG, 128)
                .childOption(ChannelOption.SO_KEEPALIVE, true);

        String[] array = serverAddress.split(":");
        String host = array[0];
        int port = Integer.parseInt(array[1]);

        ChannelFuture future = bootstrap.bind(host, port).sync();
        if (serviceRegistry != null) {
            serviceRegistry.register(serverAddress);
        }
        future.channel().closeFuture().sync();
    }

    public void addService(String interfaceName, Object serviceBean) {
        if (!handlerMap.containsKey(interfaceName)) {
            handlerMap.put(interfaceName, serviceBean);
        }
    }


    public void stop() {
        if (bossGroup != null) {
            bossGroup.shutdownGracefully();
        }
        if (workerGroup != null) {
            workerGroup.shutdownGracefully();
        }
    }


    public static <T> void publishService(T service, Class<T> serviceClass){
    }

    public static <T> T discoveryService(Class<T> serviceClass){
        return null;
    }

    public static <T> T discoveryService(Class<T> serviceClass, Integer balancedType){
        return null;
    }

}
