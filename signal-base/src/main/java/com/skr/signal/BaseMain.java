package com.skr.signal;


import com.skr.signal.base.rpc.client.RpcNettyClient;
import com.skr.signal.base.rpc.server.RpcServer;
import com.skr.signal.common.service.HelloWorldService;

/**
 * @author mqw
 * @create 2020-06-25-08:59
 */
public class BaseMain {

    public static void main(String[] args) throws InterruptedException {
        RpcServer.getInstance();
//        System.out.println(instance);
//        RpcClient.create(HelloWorldService.class);
//        HelloWorldService proxy = RpcNettyClient.createProxy(HelloWorldService.class);
//        proxy.helloWorld("???");
    }

}
