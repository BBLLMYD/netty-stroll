package com.skr.signal.base.rpc.client.proxy;

import com.skr.signal.base.rpc.client.RpcNettyClient;
import com.skr.signal.base.rpc.letter.RpcRequest;
import com.skr.signal.base.rpc.letter.RpcResponse;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * @author mqw
 * @create 2020-06-22-14:04
 */
public class RpcClient implements InvocationHandler {

    private static final RpcClient rpcClient = new RpcClient();

    @SuppressWarnings("unchecked")
    public   <T> T create(Class<T> interfaceClass) {
        T o = (T)Proxy.newProxyInstance(
                interfaceClass.getClassLoader(),
                new Class<?>[]{interfaceClass},
                this);
        return o;
    }

    private RpcClient(){};

    public static RpcClient getInstance(){
        return rpcClient;
    }


    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Exception {
        if (Object.class == method.getDeclaringClass()) {
            String name = method.getName();
            if ("equals".equals(name)) {
                return proxy == args[0];
            } else if ("hashCode".equals(name)) {
                return System.identityHashCode(proxy);
            } else if ("toString".equals(name)) {
                return proxy.getClass().getName() + "@" +
                        Integer.toHexString(System.identityHashCode(proxy)) +
                        ", with InvocationHandler " + this;
            } else {
                throw new IllegalStateException(String.valueOf(method));
            }
        }
        RpcRequest request = new RpcRequest();
        request.setTraceId(UUID.randomUUID().toString());
        request.setClassName(method.getDeclaringClass().getName());
        request.setMethodName(method.getName());
        request.setParameterTypes(method.getParameterTypes());
        request.setParameters(args);
        CompletableFuture<RpcResponse> future = RpcNettyClient.getInstance().sendRpcRequest(request);
        RpcResponse response = future.get();
        if(StringUtils.isNotEmpty(response.getError())){
            return response.getError();
        }
        return response.getResult();
    }


}
