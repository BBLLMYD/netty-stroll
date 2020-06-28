package com.skr.signal;


import com.skr.signal.base.rpc.server.RpcServer;

/**
 * @author mqw
 * @create 2020-06-25-08:59
 */
public class BaseMain {

    public static void main(String[] args) throws InterruptedException {
        RpcServer instance = RpcServer.run();
    }

}
