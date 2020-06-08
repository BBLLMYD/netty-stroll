package com.skr.signal.core.proxy;

import java.lang.reflect.Proxy;

/**
 * @author mqw
 * @create 2020-06-08-18:14
 */
public class RpcClient {

    public static <T> T clientProxy(Class<?> interfaces){
        return (T) Proxy.newProxyInstance(interfaces.getClassLoader(), new Class<?>[]{interfaces}, new RemoteInvocationHandler(interfaces));
    }
}
