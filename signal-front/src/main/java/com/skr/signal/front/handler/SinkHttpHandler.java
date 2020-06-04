package com.skr.signal.front.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandler;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;

/**
 * @author mqw
 * @create 2020-06-04-17:42
 */
public class SinkHttpHandler extends SimpleChannelInboundHandler<HttpObject> {

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, HttpObject httpObject) throws Exception {
        if(httpObject instanceof HttpRequest) {
            // 将请求内容转换协议，下沉到core层
            System.out.println("将请求内容转换协议，下沉到core层");
        }
    }



}
