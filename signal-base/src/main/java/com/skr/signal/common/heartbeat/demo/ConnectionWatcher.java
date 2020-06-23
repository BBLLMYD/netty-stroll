package com.skr.signal.common.heartbeat.demo;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.util.Timeout;
import io.netty.util.Timer;
import io.netty.util.TimerTask;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author mqw
 * @create 2020-06-02-15:30
 */
@ChannelHandler.Sharable
public abstract class ConnectionWatcher  extends ChannelInboundHandlerAdapter implements TimerTask {

    private final Bootstrap bootstrap;
    private final Timer timer;
    private final int port;
    private final String host;
    private volatile boolean reconnect;
    private AtomicInteger attempts;

    public ConnectionWatcher(Bootstrap bootstrap, Timer timer, int port, String host, boolean reconnect) {
        this.bootstrap = bootstrap;
        this.timer = timer;
        this.port = port;
        this.host = host;
        this.reconnect = reconnect;
    }


    /**
     * channel链路每次active的时候，将其连接的次数重新☞ 0
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

        System.out.println("当前链路已经激活了，重连尝试次数重新置为0");

        attempts = new AtomicInteger(0);
        ctx.fireChannelActive();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("链接关闭");
        if(reconnect){
            System.out.println("链接关闭，将进行重连");
            if (attempts.get() < 12) {
                attempts.getAndIncrement();
                //重连的间隔时间会越来越长
                int timeout = 2 << attempts.get();
                timer.newTimeout(this, timeout, TimeUnit.MILLISECONDS);
            }
        }
        ctx.fireChannelInactive();
    }


    @Override
    public void run(Timeout timeout) throws Exception {
        ChannelFuture future;
        //bootstrap已经初始化好了，只需要将handler填入就可以了
        synchronized (bootstrap) {
            bootstrap.handler(new ChannelInitializer<Channel>() {
                @Override
                protected void initChannel(Channel ch) throws Exception {
                    ch.pipeline().addLast(handlers());
                }
            });
            future = bootstrap.connect(host,port);
        }
        //future对象
        future.addListener((ChannelFutureListener) f -> {
            boolean succeed = f.isSuccess();

            //如果重连失败，则调用ChannelInactive方法，再次出发重连事件，一直尝试12次，如果失败则不再重连
            if (!succeed) {
                System.out.println("重连失败");
                f.channel().pipeline().fireChannelInactive();
            }else{
                System.out.println("重连成功");
            }
        });
    }

    abstract ChannelHandler[] handlers();
}
