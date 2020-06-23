package com.skr.signal.base.rpc.client.proxy;


import com.skr.signal.base.rpc.client.RPCFuture;

/**
 * Created by luxiaoxun on 2016/3/16.
 */
public interface IAsyncObjectProxy {
    RPCFuture call(String funcName, Object... args);
}