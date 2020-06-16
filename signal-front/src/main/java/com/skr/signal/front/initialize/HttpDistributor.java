package com.skr.signal.front.initialize;

import com.skr.signal.front.async.AsyncHandleUnit;
import com.skr.signal.front.async.AsyncResponseManager;
import com.skr.signal.front.content.Constant;
import com.skr.signal.front.exception.ServiceException;
import com.skr.signal.front.handler.Handler;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;
import java.net.URI;
import java.util.Objects;


/**
 * @author mqw
 *
 * 当前handler会有几个实例，取决于workerGroup设置的线程数量
 * *****重点*****：整个pipeline都是在同一个线程里完成的，
 *  如果当前pipeline流程中有阻塞状态的线程，此时后续的请求不会开启新的worker线程处理！！而是同步阻塞！！
 *      （因为当线程阻塞的时候netty的设计认为你是在进行处理IO读写操作）
 *  如果当前pipeline流程中的线程处于Runnable的忙碌状态，后续的请求才会开启新的线程处理！！
 *      （并会新建当前pipeline下的handler对象）
 *  所以在IO读写的handler中，尽量不要做其他有阻塞行为动作！！可以交给其他线程池异步隔离！！
 *  将handler线程阻塞会严重影响吞吐率！！
 *
 * @create 2020-06-04-10:28
 */
@Slf4j
public class HttpDistributor extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext channelHandlerContext, Object httpObject) throws Exception {

        if(httpObject instanceof FullHttpRequest) {

            FullHttpRequest httpRequest = (FullHttpRequest) httpObject;

            // 获取uri, 过滤指定的资源
            URI uri = new URI(httpRequest.uri());
            String path = uri.getPath();
            Handler handler = HandlerInitializer.getHandler(path);
            if(Objects.isNull(handler)) {
                channelHandlerContext.channel().close();
                return;
            }

            System.out.println(channelHandlerContext.hashCode());
            System.out.println(channelHandlerContext.pipeline().hashCode());
            System.out.println(this.hashCode());
            // boolean flag = 1 == 1;
            // while (flag){}

            // 获取请求体
            ByteBuf buf = httpRequest.content();
            String reqContent = buf.toString(CharsetUtil.UTF_8);

            // 构建异步响应单元，交给处理器
            AsyncHandleUnit handleUnit = AsyncHandleUnit.builder()
                    .channelHandlerContext(channelHandlerContext)
                    .content(reqContent)
                    .handler(handler)
                    .build();
            AsyncResponseManager.asyncResponse(handleUnit);
            ReferenceCountUtil.release(buf);

        }else {
            // 不规范请求直接关闭
            channelHandlerContext.channel().close();
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error(Constant.SERVER_ERROR,cause);
        String msg;
        if(cause instanceof ServiceException){
            msg = ((ServiceException) cause).getMsg();
        }else {
            msg = "内部错误";
        }
        ctx.writeAndFlush(HttpResponseBuilder.buildResponse(msg));
        ctx.channel().close();
    }

}
