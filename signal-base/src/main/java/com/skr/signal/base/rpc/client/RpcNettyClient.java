package com.skr.signal.base.rpc.client;

import com.skr.signal.base.registry.LoadPolicy;
import com.skr.signal.base.registry.ServiceDiscover;
import com.skr.signal.base.registry.impl.load.RandomLoadPolicy;
import com.skr.signal.base.registry.impl.zookeeper.ZKServiceDiscovery;
import com.skr.signal.base.rpc.client.proxy.RpcClient;
import com.skr.signal.base.rpc.letter.RpcRequest;
import com.skr.signal.base.rpc.letter.RpcResponse;
import com.skr.signal.common.util.PropertiesUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.net.InetSocketAddress;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;

/**
 * @author mqw
 * @create 2020-06-22-13:51
 */
@Slf4j
public final class RpcNettyClient {

    private static Bootstrap bootstrap;
    private static EventLoopGroup eventLoopGroup;
    private static RpcNettyClient rpcNettyClient;

    @Setter
    private ServiceDiscover serviceDiscover;

    static {
        PropertiesUtil propertiesUtil = PropertiesUtil.newInstance("config-rpc.properties");
        String registrationAddress = propertiesUtil.readProperty("registration.address");

        rpcNettyClient = new RpcNettyClient();

        LoadPolicy loadPolicy;
        ServiceDiscover discover;
        String loadPolicyImplCfg      = propertiesUtil.readProperty("loadPolicy.impl");
        String serviceDiscoverImplCfg = propertiesUtil.readProperty("serviceDiscover.impl");

        try {
            if (StringUtils.isEmpty(loadPolicyImplCfg)) {
                loadPolicy = new RandomLoadPolicy();
            } else {
                loadPolicy = (LoadPolicy) Class.forName(loadPolicyImplCfg).newInstance();
            }
            if (StringUtils.isEmpty(serviceDiscoverImplCfg)) {
                discover = new ZKServiceDiscovery();
            } else {
                discover = (ServiceDiscover) Class.forName(serviceDiscoverImplCfg).newInstance();
            }
        } catch (Exception e) {
            throw new RuntimeException("初始化服务发现组件期间异常",e);
        }
        rpcNettyClient.setServiceDiscover(discover.loadPolicy(loadPolicy).registryAddress(registrationAddress));
        eventLoopGroup = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        bootstrap.group(eventLoopGroup)
                .channel(NioSocketChannel.class)
                .handler(new LoggingHandler(LogLevel.INFO))
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                .handler(new RpcClientInitializer());
    }

    private RpcNettyClient() {
    }

    public static RpcNettyClient getInstance() {
        return rpcNettyClient;
    }

    Channel doConnect(InetSocketAddress inetSocketAddress) throws ExecutionException, InterruptedException {
        CompletableFuture<Channel> completableFuture = new CompletableFuture<>();
        bootstrap.connect(inetSocketAddress).addListener((ChannelFutureListener) future -> {
            if (future.isSuccess()) {
                log.info("当前客户端向服务端[{}]发起连接成功:", inetSocketAddress.toString());
                completableFuture.complete(future.channel());
            } else {
                throw new IllegalStateException();
            }
        });
        return completableFuture.get();
    }


    public CompletableFuture<RpcResponse> sendRpcRequest(RpcRequest rpcRequest) {
        // 构建返回值
        CompletableFuture<RpcResponse> resultFuture = new CompletableFuture<>();
        String discover = serviceDiscover.discover(rpcRequest.getClassName());
        String[] address = discover.split(":");

        Channel channel = ChannelManager.getChannel(new InetSocketAddress(address[0], Integer.valueOf(address[1])));

        CountDownLatch latch = new CountDownLatch(1);

        if (channel != null && channel.isActive()) {
            // 放入未处理的请求
            AsyncResultContainer.put(rpcRequest.getTraceId(), resultFuture);
            channel.writeAndFlush(rpcRequest).addListener((ChannelFutureListener) future -> {
                if (future.isSuccess()) {
                    log.info("client send message: [{}]", rpcRequest);
                } else {
                    future.channel().close();
                    resultFuture.completeExceptionally(future.cause());
                    log.error("Send failed:", future.cause());
                }
                latch.countDown();
            });
        } else {
            throw new IllegalStateException();
        }
        try {
            latch.await();
        } catch (InterruptedException e) {
            log.error(" wait result error : ", e);
        }
        return resultFuture;
    }

    public static <T> T createProxy(Class<T> interfaceClass) {
        return RpcClient.getInstance().create(interfaceClass);
    }


}
