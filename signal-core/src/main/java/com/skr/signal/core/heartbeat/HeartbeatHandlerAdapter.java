package com.skr.signal.core.heartbeat;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleStateEvent;

/**
 * @author mqw
 * @create 2020-06-01-10:21 下午
 */
public class HeartbeatHandlerAdapter extends ChannelInboundHandlerAdapter {

    /**
     * 心跳检测回调
     *
     * @param ctx
     * @param evt
     * @throws Exception
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {

        if(evt instanceof IdleStateEvent){
            IdleStateEvent event = (IdleStateEvent) evt;
            String eventType = null;
            switch (event.state()) {
                case READER_IDLE:
                    eventType = "read free";
                    break;
                case WRITER_IDLE:
                    eventType = "write free";
                    break;
                case ALL_IDLE:
                    eventType = "read-write free";
                    break;
            }
            System.out.println("检测到心跳异常链接事件："+eventType);
        }
        // 关闭此通道
        Channel channel = ctx.channel();
        channel.close();

    }


}
