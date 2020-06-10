package com.skr.signal.front.initialize;

import com.skr.signal.front.handler.Handler;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.util.CharsetUtil;
import lombok.Builder;
import lombok.Data;

import java.util.concurrent.Callable;


/**
 * @author mqw
 * @create 2020-06-10-11:20
 */
@Data
@Builder
public class AsyncHandleUnit implements Callable<Boolean> {

    ChannelHandlerContext channelHandlerContext;
    ByteBuf buf;
    Handler handler;

    @Override
    public Boolean call() {
        String reqContent = buf.toString(CharsetUtil.UTF_8);
        String data = handler.getAnswer(reqContent);
        /* 将响应数据封装成默认httpResponse */
        FullHttpResponse response = HttpResponseBuilder.buildResponse(data);
        this.channelHandlerContext.writeAndFlush(response);
        return true;
    }
}
