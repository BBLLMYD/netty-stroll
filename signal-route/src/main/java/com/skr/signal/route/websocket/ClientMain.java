package com.skr.signal.route.websocket;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * @author mqw
 * @create 2020-06-17-15:52
 */
public class ClientMain {

    public static void main(String[] args) throws URISyntaxException, InterruptedException {
        EventLoopGroup group = new NioEventLoopGroup();
        Bootstrap boot = new Bootstrap();
        boot.option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.TCP_NODELAY, true)
                .group(group)
                .handler(new LoggingHandler(LogLevel.INFO))
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) {
                        ChannelPipeline p = socketChannel.pipeline();
                        p.addLast(new HttpClientCodec(),
                                new HttpObjectAggregator(1024 * 1024 * 10));
                        p.addLast("hookedHandler", new WebSocketClientHandler());
                    }
                });
        URI websocketURI = new URI("ws://127.0.0.1:9001/ws");
        HttpHeaders httpHeaders = new DefaultHttpHeaders();


        WebSocketClientHandshaker handshaker
                = WebSocketClientHandshakerFactory.newHandshaker(websocketURI, WebSocketVersion.V13, null, true, httpHeaders);
        System.out.println("connect");
        final Channel channel = boot.connect(websocketURI.getHost(), websocketURI.getPort()).sync().channel();
        WebSocketClientHandler handler = (WebSocketClientHandler) channel.pipeline().get("hookedHandler");
        handler.setHandshaker(handshaker);
        handshaker.handshake(channel);
        //阻塞等待是否握手成功
        handler.handshakeFuture().sync();


        Thread text = new Thread(() -> {
            int i = 30;
            while (i > 0) {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                }
                System.out.println("text send");
                TextWebSocketFrame frame = new TextWebSocketFrame("我是文本");
                channel.writeAndFlush(frame).addListener((ChannelFutureListener) channelFuture -> {
                    if (channelFuture.isSuccess()) {
                        System.out.println("text send success");
                    } else {
                        System.out.println("text send failed  " + channelFuture.cause().getMessage());
                    }
                });
            }
        });


        Thread bina = new Thread(() -> {
            File file = new File("C:\\Users\\Administrator\\Desktop\\test.wav");
            FileInputStream fin = null;
            try {
                fin = new FileInputStream(file);
                int len = 0;
                byte[] data = new byte[1024];
                while ((len = fin.read(data)) > 0) {
                    ByteBuf bf = Unpooled.buffer().writeBytes(data);
                    BinaryWebSocketFrame binaryWebSocketFrame = new BinaryWebSocketFrame(bf);
                    channel.writeAndFlush(binaryWebSocketFrame).addListener(new ChannelFutureListener() {
                        @Override
                        public void operationComplete(ChannelFuture channelFuture) throws Exception {
                            if (channelFuture.isSuccess()) {
                                System.out.println("bina send success");
                            } else {
                                System.out.println("bina send failed  " + channelFuture.cause().toString());
                            }
                        }
                    });
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        text.start();
//        bina.start();
    }
}
