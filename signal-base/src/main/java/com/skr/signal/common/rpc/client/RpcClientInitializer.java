package com.skr.signal.common.rpc.client;

import com.skr.signal.common.rpc.letter.RpcDecoder;
import com.skr.signal.common.rpc.letter.RpcEncoder;
import com.skr.signal.common.rpc.letter.RpcRequest;
import com.skr.signal.common.rpc.letter.RpcResponse;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

/**
 * @author mqw
 * @create 2020-06-22-13:55
 */
public class RpcClientInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        ChannelPipeline cp = socketChannel.pipeline();
        cp.addLast(new RpcEncoder(RpcRequest.class));
        cp.addLast(new LengthFieldBasedFrameDecoder(65536, 0, 4, 0, 0));
        cp.addLast(new RpcDecoder(RpcResponse.class));
        cp.addLast(new RpcClientHandler());
    }
}