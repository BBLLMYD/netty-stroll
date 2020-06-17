package com.skr.signal.route.websocket;

import io.netty.channel.*;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.util.CharsetUtil;

/**
 * @author mqw
 * @create 2020-06-17-15:57
 */
public class WebSocketClientHandler extends SimpleChannelInboundHandler<Object> {

    WebSocketClientHandshaker handshaker;
    ChannelPromise handshakeFuture;

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) {
        this.handshakeFuture = ctx.newPromise();
    }
    public WebSocketClientHandshaker getHandshaker() {
        return handshaker;
    }

    public void setHandshaker(WebSocketClientHandshaker handshaker) {
        this.handshaker = handshaker;
    }

    public ChannelPromise getHandshakeFuture() {
        return handshakeFuture;
    }

    public void setHandshakeFuture(ChannelPromise handshakeFuture) {
        this.handshakeFuture = handshakeFuture;
    }

    public ChannelFuture handshakeFuture() {
        return this.handshakeFuture;
    }


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("channelRead0  "+this.handshaker.isHandshakeComplete());
        Channel ch = ctx.channel();
        FullHttpResponse response;
        if (!this.handshaker.isHandshakeComplete()) {
            try {
                response = (FullHttpResponse)msg;
                //握手协议返回，设置结束握手
                this.handshaker.finishHandshake(ch, response);
                //设置成功
                this.handshakeFuture.setSuccess();
                System.out.println("WebSocket Client connected! response headers[sec-websocket-extensions]:{}"+response.headers());
            } catch (WebSocketHandshakeException var7) {
                FullHttpResponse res = (FullHttpResponse)msg;
                String errorMsg = String.format("WebSocket Client failed to connect,status:%s,reason:%s", res.status(), res.content().toString(CharsetUtil.UTF_8));
                this.handshakeFuture.setFailure(new Exception(errorMsg));
            }
        } else if (msg instanceof FullHttpResponse) {
            response = (FullHttpResponse)msg;
            //this.listener.onFail(response.status().code(), response.content().toString(CharsetUtil.UTF_8));
            throw new IllegalStateException("Unexpected FullHttpResponse (getStatus=" + response.status() + ", content=" + response.content().toString(CharsetUtil.UTF_8) + ')');
        } else {WebSocketFrame frame = (WebSocketFrame)msg;
            if (frame instanceof TextWebSocketFrame) {
                TextWebSocketFrame textFrame = (TextWebSocketFrame)frame;
                //this.listener.onMessage(textFrame.text());
                System.err.println("TextWebSocketFrame  "+textFrame.text());
            } else if (frame instanceof BinaryWebSocketFrame) {
                BinaryWebSocketFrame binFrame = (BinaryWebSocketFrame)frame;
                System.out.println("BinaryWebSocketFrame");
            } else if (frame instanceof PongWebSocketFrame) {
                System.out.println("WebSocket Client received pong");
            } else if (frame instanceof CloseWebSocketFrame) {
                System.out.println("receive close frame");
                //this.listener.onClose(((CloseWebSocketFrame)frame).statusCode(), ((CloseWebSocketFrame)frame).reasonText());
                ch.close();
            }
        }
    }
}
