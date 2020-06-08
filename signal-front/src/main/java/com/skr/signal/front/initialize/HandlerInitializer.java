package com.skr.signal.front.initialize;

import com.skr.signal.front.exception.ServiceException;
import com.skr.signal.front.handler.Handler;
import org.reflections.Reflections;

import java.util.HashMap;
import java.util.Objects;
import java.util.Set;

/**
 * @author mqw
 * @create 2020-06-04-11:12 下午
 */
public class HandlerInitializer {

    private static final HashMap<String, Handler> urlHandlerMapping = new HashMap<>();

    static {
        init();
    }

    private static void init()  {
        String packageName = "com.skr.signal.front.handler.impl";
        Reflections f = new Reflections(packageName);
        Set<Class<?>> set = f.getTypesAnnotatedWith(com.skr.signal.front.anno.HandlerTag.class);
        for (Class<?> aClass : set) {
            String path = aClass.getAnnotation(com.skr.signal.front.anno.HandlerTag.class).path();
            Handler handler;
            try {
                handler = (Handler)aClass.newInstance();
            } catch (Exception e) {
                continue;
            }
            if(!Objects.isNull(urlHandlerMapping.get(path))){
                throw new ServiceException("处理器路径存在冲突："+path);
            }
            urlHandlerMapping.put(path,handler);
        }
    }

    public static Handler getHandler(String url){
       return urlHandlerMapping.get(url);
    }


}
