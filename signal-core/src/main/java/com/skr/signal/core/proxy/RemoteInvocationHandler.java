package com.skr.signal.core.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * @author mqw
 * @create 2020-06-08-18:29
 */
public class RemoteInvocationHandler<T> implements InvocationHandler {

    private Class<T> clazz;

    public RemoteInvocationHandler(Class<T> clazz) {
        this.clazz = clazz;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        return null;
    }

}
