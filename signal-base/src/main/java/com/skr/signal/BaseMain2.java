package com.skr.signal;


import com.skr.signal.base.rpc.client.RpcNettyClient;
import com.skr.signal.common.service.HelloWorldService;

/**
 * @author mqw
 * @create 2020-06-25-08:59
 */
public class BaseMain2 {

    public static void main(String[] args) {
        HelloWorldService proxy = RpcNettyClient.createProxy(HelloWorldService.class);
        String s = proxy.helloWorld("???");
        System.out.println(s);
    }

}
