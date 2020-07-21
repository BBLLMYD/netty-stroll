package com.skr.signal.data.websocket;

import com.skr.signal.data.websocket.initialize.WebSocketChanelInitializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * @author mqw
 * @create 2020-06-17-16:58
 */
public class WebSocketBoot {

    public static void main(String[] args) {
        EventLoopGroup boss = new NioEventLoopGroup(5);
        EventLoopGroup worker = new NioEventLoopGroup(3);
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(boss,worker)
                    .channel(NioServerSocketChannel.class)
                    .childOption(ChannelOption.TCP_NODELAY, true)
                    .childHandler(new WebSocketChanelInitializer());
            bootstrap.bind(9001).sync().channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }
    }

}
