package com.skr.signal.common.rpc.client.proxy;


import com.skr.signal.common.rpc.client.RPCFuture;

/**
 * Created by luxiaoxun on 2016/3/16.
 */
public interface IAsyncObjectProxy {
    RPCFuture call(String funcName, Object... args);
}