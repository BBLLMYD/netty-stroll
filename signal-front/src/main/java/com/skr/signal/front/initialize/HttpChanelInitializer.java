package com.skr.signal.front.initialize;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.*;

/**
 * @author mqw
 * @create 2020-06-04-10:13
 */
public class HttpChanelInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        // 得到管道
        System.out.println(Thread.currentThread().getId()+","+this.hashCode());
        ChannelPipeline pipeline = socketChannel.pipeline();
        pipeline.addLast("http-codec", new HttpServerCodec());
        pipeline.addLast("http-chunk-aggregator",new HttpObjectAggregator(65535));
        // 增加自定义的分发器handler
        pipeline.addLast("distribute-handler", new HttpDistributor());
    }

}
