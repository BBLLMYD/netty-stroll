package com.skr.signal.core.initialize;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;

/**
 * @author mqw
 * @create 2020-06-09-11:48
 */
public class TcpChanelInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ch.pipeline().addLast(new TcpGovernHandler());
    }
}
