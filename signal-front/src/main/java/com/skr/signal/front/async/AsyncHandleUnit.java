package com.skr.signal.front.async;


import com.skr.signal.front.handler.Handler;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.CharsetUtil;
import lombok.Builder;
import lombok.Data;

import java.util.Objects;

import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_LENGTH;
import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_TYPE;


/**
 * @author mqw
 * @create 2020-06-10-11:20
 */
@Data
@Builder
public class AsyncHandleUnit implements Runnable {

    ChannelHandlerContext channelHandlerContext;
    String content;
    Handler handler;

    @Override
    public void run() {
        String data = null;
        try {
             data = handler.getAnswer(content);
            /* 将响应数据封装成默认httpResponse */
        }catch (Exception e){
            data = "server error";
        }finally {
            FullHttpResponse response = buildResponse(data);
            this.channelHandlerContext.channel().writeAndFlush(response);
        }
        /* 当客户端由于超时等原因链接断开时isSuccess为false */
        // return future.isSuccess();
        /* 不关心响应动作的达到情况性，提高吞吐率。正常计算后即视为成功处理 */
    }

    public static FullHttpResponse buildResponse(String content){
        if(Objects.isNull(content)) {
            content = "null";
        }
        ByteBuf byteBuf = Unpooled.copiedBuffer(content, CharsetUtil.UTF_8);
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, byteBuf);
        response.headers().set(CONTENT_TYPE, "application/json");
        response.headers().set(CONTENT_LENGTH, byteBuf.readableBytes());
        response.headers().set("Thread",Thread.currentThread().getId()+","+Thread.currentThread().getName());
        return response;
    }
}
