package com.skr.signal.base.rpc.client.proxy;

import com.skr.signal.base.rpc.client.RpcNettyClient;
import com.skr.signal.base.rpc.letter.RpcRequest;
import com.skr.signal.base.rpc.letter.RpcResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.BiConsumer;

/**
 * @author mqw
 * @create 2020-06-22-14:04
 */
@Slf4j
public class RpcClient implements InvocationHandler {

    private static final RpcClient rpcClient = new RpcClient();

    private final static ThreadLocal<String> tagVal = new ThreadLocal<>();

    @SuppressWarnings("unchecked")
    public <T> T create(Class<T> interfaceClass) {
        T o = (T)Proxy.newProxyInstance(
                interfaceClass.getClassLoader(),
                new Class<?>[]{interfaceClass},
                this);
        log.info("构造代理对象：{}",o);
        return o;
    }

    public <T> T createWithTag(Class<T> interfaceClass,String tag) {
        T t = create(interfaceClass);
        tagVal.set(tag);
        return t;
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
        RpcResponse response = null;
        try {
            RpcRequest request = new RpcRequest();
            request.setTraceId(UUID.randomUUID().toString());
            request.setClassName(method.getDeclaringClass().getName());
            request.setMethodName(method.getName());
            request.setParameterTypes(method.getParameterTypes());
            request.setParameters(args);
            request.setTag(tagVal.get());
            CompletableFuture<RpcResponse> future = RpcNettyClient.getInstance().sendRpcRequest(request);
            response = future.get();
            if(StringUtils.isNotEmpty(response.getError())){
                return response.getError();
            }
            log.info("{}:获取远程调用结果：{}",method.getName(),response.getResult());
        } finally {
            tagVal.remove();
        }
        return response.getResult();
    }


}
