package com.skr.signal.front.initialize;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;

import java.net.URI;

/**
 * @author mqw
 * @create 2020-06-04-10:28
 */
public class CheckHttpHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext channelHandlerContext, Object httpObject) throws Exception {
        //判断 msg 是不是 httprequest请求
        if(httpObject instanceof FullHttpRequest) {

            System.out.println("ctx 类型="+channelHandlerContext.getClass());
            System.out.println("pipeline hashcode" + channelHandlerContext.pipeline().hashCode() + " TestHttpServerHandler hash=" + this.hashCode());
            System.out.println("msg 类型=" + httpObject.getClass());
            System.out.println("客户端地址" + channelHandlerContext.channel().remoteAddress());

            //获取到
            FullHttpRequest httpRequest = (FullHttpRequest) httpObject;
            //获取uri, 过滤指定的资源
            URI uri = new URI(httpRequest.uri());
            if("/favicon.ico".equals(uri.getPath())) {
                channelHandlerContext.channel().close();
                return;
            }

            httpRequest.content();

            // 2.获取请求体
            ByteBuf buf = httpRequest.content();
            String reqContent = buf.toString(CharsetUtil.UTF_8);

            // 3.获取请求方法
            HttpMethod method = httpRequest.method();

            // 4.获取请求头
            HttpHeaders headers = httpRequest.headers();

            // 5.根据method，确定不同的逻辑
            if(method.equals(HttpMethod.GET)){
                // TODO
            }

            ByteBuf content = Unpooled.copiedBuffer("hello, 我是服务器", CharsetUtil.UTF_8);
            FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, content);
            response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain");
            response.headers().set(HttpHeaderNames.CONTENT_LENGTH, content.readableBytes());
            channelHandlerContext.writeAndFlush(response);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
    }
}
