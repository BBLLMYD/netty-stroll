package com.skr.signal.data.websocket.initialize;

import com.skr.signal.data.websocket.handler.WebSocketHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;

/**
 * @author mqw
 * @create 2020-06-12-11:22
 */
public class WebSocketChanelInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel ch) {

        ChannelPipeline pipeline = ch.pipeline();
        // websocket是由http协议升级而来，所以需要http的编解码器
        pipeline.addLast(new HttpServerCodec());
        // websocket是以块的方式写，相应的处理器
        pipeline.addLast(new ChunkedWriteHandler());
        // 处理器可以将多段数据聚合 避免数据量大 http分段
        pipeline.addLast(new HttpObjectAggregator(10240));
        // websocket处理的path
        pipeline.addLast(new WebSocketServerProtocolHandler("/ws"));
        // 自定义处理器
        pipeline.addLast(new WebSocketHandler());

    }
}
