package com.skr.signal.base.rpc.server;

import com.skr.signal.base.rpc.letter.RpcRequest;
import com.skr.signal.base.rpc.letter.RpcResponse;
import com.skr.signal.common.Constant;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Objects;

/**
 * @author mqw
 * @create 2020-06-23-13:20
 */
@Slf4j
public class RpcHandler extends SimpleChannelInboundHandler<RpcRequest> {

    private final Map<String, Object> handlerMap;

    public RpcHandler(Map<String, Object> handlerMap) {
        this.handlerMap = handlerMap;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcRequest request) throws Exception {
        if (Constant.TAG_KEEP_ALIVE.equals(request.getTag())) {
            log.info("收到服务调用侧 {} 的心跳包，{}",ctx.channel().remoteAddress(),System.currentTimeMillis());
            return;
        }

        RpcResponse response = new RpcResponse();
        response.setTraceId(request.getTraceId());
        try {
            response.setResult(handle(request));
        }catch (Exception e){
            response.setError(e.getMessage());
        }
        ctx.writeAndFlush(response);
    }

    public Object handle(RpcRequest request) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        String className   = request.getClassName();
        Object serviceBean = handlerMap.get(className);
        if(Objects.isNull(serviceBean)){
            throw new RuntimeException("无"+className+"接口");
        }
        Class<?>   serviceClass   = serviceBean.getClass();
        String     methodName     = request.getMethodName();
        Class<?>[] parameterTypes = request.getParameterTypes();
        Object[]   parameters     = request.getParameters();
        // JDK reflect
        Method method = serviceClass.getMethod(methodName, parameterTypes);
        method.setAccessible(true);
        return method.invoke(serviceBean, parameters);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        ctx.close();
    }
}
