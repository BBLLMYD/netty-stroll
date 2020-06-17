package com.skr.signal.front.async;


import com.skr.signal.front.handler.Handler;
import com.skr.signal.front.initialize.HttpResponseBuilder;
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
    String content;
    Handler handler;

    @Override
    public Boolean call() {
        String data = handler.getAnswer(content);
        /* 将响应数据封装成默认httpResponse */
        FullHttpResponse response = HttpResponseBuilder.buildResponse(data);
        this.channelHandlerContext.channel().writeAndFlush(response);
        /* 当客户端由于超时等原因链接断开时isSuccess为false */
        // return future.isSuccess();
        /* 不关心响应动作的达到情况性，提高吞吐率。正常计算后即视为成功处理 */
        return true;
    }
}
