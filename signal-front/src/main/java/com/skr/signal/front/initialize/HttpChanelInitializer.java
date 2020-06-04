package com.skr.signal.front.initialize;

import com.skr.signal.front.handler.CheckHttpHandler;
import com.skr.signal.front.handler.SinkHttpHandler;
import com.sun.net.httpserver.HttpServer;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.string.StringDecoder;

import java.nio.charset.Charset;

/**
 * @author mqw
 * @create 2020-06-04-10:13
 */
public class HttpChanelInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        //得到管道
        ChannelPipeline pipeline = socketChannel.pipeline();

        pipeline.addLast("http-decoder", new HttpRequestDecoder());
        pipeline.addLast("http-chunk-aggregator",new HttpObjectAggregator(65535));
        pipeline.addLast("http-encoder", new HttpResponseEncoder());
        //2. 增加自定义的handler
        pipeline.addLast("CheckHandler", new CheckHttpHandler());
        pipeline.addLast("SinkHandler", new SinkHttpHandler());
    }

}
