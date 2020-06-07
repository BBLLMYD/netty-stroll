package com.skr.signal.front.handler;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import static com.alibaba.fastjson.JSONObject.*;

/**
 * 自定义接口处理器需要继承本类
 *
 * @author mqw
 * @create 2020-06-04-20:02
 */
public abstract class Handler<Request,Response> {

    protected abstract Response handle(Request request) throws Exception;

    private Class<Request> deSerializable() {
        Type type = this.getClass().getGenericSuperclass();
        if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) type;
            return (Class<Request>) parameterizedType.getActualTypeArguments()[0];
        }
        throw new RuntimeException("处理器未继承com.skr.signal.front.handler.Handler");
    }

    public String getAnswer(String json) {
        Request request = getRequestTypeData(json);
        Response response = buildResponseTypeData(request);
        String jsonStr = toJSONString(response);
        return jsonStr;
    }

    private Response buildResponseTypeData(Request request) {
        Response response;
        try {
            response = handle(request);
        } catch (Throwable e) {
            throw new RuntimeException("请求处理异常",e);
        }
        return response;
    }

    public Request getRequestTypeData(String json) {
        Request requestByFastJson;
        try {
            requestByFastJson = parseObject(json, deSerializable());
        }catch (Exception e){
            throw new RuntimeException("请求数据格式异常",e);
        }
        return requestByFastJson;
    }

}
