package com.skr.signal.common.base;

import com.skr.signal.common.heartbeat.HeartbeatServerHandlerDefault;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

/**
 * @author mqw
 * @create 2020-06-02-11:44
 */
public class BaseServerInitializerWraper {

    public static ChannelPipeline buildBaseInitializer(ChannelPipeline pipeline){
        // 检测 读，写，读+写，超时无对应事件的时候 发送心跳包
        pipeline.addLast("idleStateHandler",new IdleStateHandler(1,1,1, TimeUnit.MINUTES));
        pipeline.addLast("idleStateTrigger",new HeartbeatServerHandlerDefault());
        pipeline.addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4));
        pipeline.addLast(new LengthFieldPrepender(4));
        return pipeline;
    }

}
