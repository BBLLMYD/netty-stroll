package com.skr.signal.front.initialize;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.CharsetUtil;

import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_LENGTH;
import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_TYPE;

/**
 * @author mqw
 * @create 2020-06-10-11:51
 */
public class HttpResponseBuilder {

    public static FullHttpResponse buildResponse(String content){
        ByteBuf byteBuf = Unpooled.copiedBuffer(content, CharsetUtil.UTF_8);
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, byteBuf);
        response.headers().set(CONTENT_TYPE, "application/json");
        response.headers().set(CONTENT_LENGTH, byteBuf.readableBytes());
        response.headers().set("Thread",Thread.currentThread().getId()+","+Thread.currentThread().getName());
        return response;
    }

}
