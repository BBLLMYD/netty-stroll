package com.skr.signal.base.rpc.client;

import com.skr.signal.base.rpc.letter.RpcRequest;
import com.skr.signal.base.rpc.letter.RpcResponse;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.SocketAddress;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

/**
 * @author mqw
 * @create 2020-06-22-13:47
 */
public class RpcClientHandler extends SimpleChannelInboundHandler<RpcResponse> {
    private static final Logger logger = LoggerFactory.getLogger(RpcClientHandler.class);

    private volatile Channel channel;

    public Channel getChannel() {
        return channel;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);
        this.channel = ctx.channel();
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, RpcResponse response) throws Exception {
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("client caught exception", cause);
        ctx.close();
    }


    public RpcResponse sendRequest(RpcRequest request) {
        return null;
    }
}