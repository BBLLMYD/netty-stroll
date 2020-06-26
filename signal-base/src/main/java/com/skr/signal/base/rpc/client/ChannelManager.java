package com.skr.signal.base.rpc.client;

import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @auther mqw
 * @date 2020-06-25
 **/
@Slf4j
public class ChannelManager {

    private static Map<String, Channel> channels = new ConcurrentHashMap<>();
    private static RpcNettyClient rpcNettyClient = RpcNettyClient.getInstance();

    public static Channel getChannel(InetSocketAddress inetSocketAddress) {
        String key = inetSocketAddress.toString();
        if (channels.containsKey(key)) {
            Channel channel = channels.get(key);
            if (Objects.nonNull(channel) && channel.isActive()) {
                return channel;
            }
            remove(inetSocketAddress);
        }
        Channel channel;
        try {
            channel = rpcNettyClient.doConnect(inetSocketAddress);
        } catch (Exception e) {
            log.error("连接服务端异常",e);
            throw new RuntimeException("连接服务端异常");
        }
        channels.put(key, channel);
        return channel;
    }

    private static void remove(InetSocketAddress inetSocketAddress) {
        String key = inetSocketAddress.toString();
        channels.remove(key);
    }

}
