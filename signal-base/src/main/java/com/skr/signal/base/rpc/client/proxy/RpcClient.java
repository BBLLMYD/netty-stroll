package com.skr.signal.base.rpc.client.proxy;

import com.skr.signal.base.registry.impl.zookeeper.ZKServiceDiscovery;
import com.skr.signal.base.rpc.client.RpcNettyClient;
import com.skr.signal.base.rpc.letter.RpcRequest;
import com.skr.signal.base.rpc.letter.RpcResponse;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * @author mqw
 * @create 2020-06-22-14:04
 */
public class RpcClient implements InvocationHandler {


    @SuppressWarnings("unchecked")
    public <T> T create(Class<T> interfaceClass) {
        return (T) Proxy.newProxyInstance(
                interfaceClass.getClassLoader(),
                new Class<?>[]{interfaceClass},
                this);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
        RpcRequest request = new RpcRequest();
        request.setTraceId(UUID.randomUUID().toString());
        request.setClassName(method.getDeclaringClass().getName());
        request.setMethodName(method.getName());
        request.setParameterTypes(method.getParameterTypes());
        request.setParameters(args);
        CompletableFuture<RpcResponse> future = RpcNettyClient.getInstance().sendRpcRequest(request);
        return null;
    }


}
