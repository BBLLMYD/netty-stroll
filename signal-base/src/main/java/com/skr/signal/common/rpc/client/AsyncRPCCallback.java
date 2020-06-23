package com.skr.signal.common.rpc.client;

/**
 * @author mqw
 * @create 2020-06-22-14:12
 */
public interface AsyncRPCCallback {

    void success(Object result);

    void fail(Exception e);

}
