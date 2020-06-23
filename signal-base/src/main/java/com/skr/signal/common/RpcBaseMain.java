package com.skr.signal.common;

import com.skr.signal.base.rpc.server.RpcServer;
import sun.jvm.hotspot.HelloWorld;

/**
 * @author mqw
 * @create 2020-05-28-16:44
 */
public class RpcBaseMain {

    public static void main(String[] args) {
        HelloWorld helloWorld = RpcServer.discoveryService(HelloWorld.class);
    }

}
