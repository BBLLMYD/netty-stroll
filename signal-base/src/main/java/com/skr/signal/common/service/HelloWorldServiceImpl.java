package com.skr.signal.common.service;

import com.skr.signal.base.rpc.server.anno.RpcServiceTag;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author mqw
 * @create 2020-06-24-12:00
 */
@RpcServiceTag(serviceName = "com.skr.signal.common.service.HelloWorldService")
public class HelloWorldServiceImpl implements HelloWorldService{

    AtomicInteger count = new AtomicInteger(0);

    @Override
    public String helloWorld(String sth) {
        String rest = "get hello " + sth + " -> " + count.getAndIncrement();
        System.out.println(rest);
        return rest;
    }
}
