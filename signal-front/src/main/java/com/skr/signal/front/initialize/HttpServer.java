package com.skr.signal.front.initialize;
import com.skr.signal.common.util.PropertiesUtil;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * @auther mqw
 * @date 2020-06-30
 **/
public class HttpServer {

    static Integer port;

    static {
        String readProperty = PropertiesUtil.newInstance("config-http.properties").readProperty("port");
        port = Integer.valueOf(readProperty);
    }

    public static void run(){
        EventLoopGroup boss = new NioEventLoopGroup(5);
        EventLoopGroup worker = new NioEventLoopGroup(3);
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(boss,worker)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new HttpChanelInitializer());
            bootstrap.bind(port).sync().channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }
    }

}
