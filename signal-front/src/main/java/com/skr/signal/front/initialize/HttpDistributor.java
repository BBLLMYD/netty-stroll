package com.skr.signal.front.initialize;

import com.skr.signal.front.content.Constant;
import com.skr.signal.front.handler.Handler;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import lombok.extern.log4j.Log4j;
import lombok.extern.slf4j.Slf4j;
import lombok.extern.slf4j.XSlf4j;
import lombok.val;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.ElementType;
import java.net.URI;
import java.util.Objects;

import static io.netty.handler.codec.http.HttpHeaderNames.*;

/**
 * @author mqw
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

            // 获取请求体
            ByteBuf buf = httpRequest.content();
            String reqContent = buf.toString(CharsetUtil.UTF_8);

            /* Handler的抽象已经封装好序列化规则 */
            String data = handler.getAnswer(reqContent);

            /* 将响应数据封装成默认httpResponse */
            FullHttpResponse response = buildResponse(data);

            channelHandlerContext.writeAndFlush(response);
        }else {
            // 不规范请求直接关闭
            channelHandlerContext.channel().close();
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error(Constant.SERVER_ERROR,cause);
        ctx.writeAndFlush(buildResponse(cause.getMessage()));
        ctx.channel().close();
    }

    private FullHttpResponse buildResponse(String content){
        ByteBuf byteBuf = Unpooled.copiedBuffer(content, CharsetUtil.UTF_8);
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, byteBuf);
        response.headers().set(CONTENT_TYPE, "application/json");
        response.headers().set(CONTENT_LENGTH, byteBuf.readableBytes());
        return response;
    }
}
