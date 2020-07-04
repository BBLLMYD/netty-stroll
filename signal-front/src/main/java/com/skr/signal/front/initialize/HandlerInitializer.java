package com.skr.signal.front.initialize;

import com.skr.signal.base.rpc.server.anno.RpcServiceTag;
import com.skr.signal.common.util.ClassUtil;
import com.skr.signal.common.util.PropertiesUtil;
import com.skr.signal.front.anno.HandlerTag;
import com.skr.signal.front.exception.ServiceException;
import com.skr.signal.front.handler.Handler;
import org.apache.commons.collections4.CollectionUtils;
import org.reflections.Reflections;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

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
        String packageName = PropertiesUtil.newInstance("config-http.properties").readProperty("front.handler.basePackage");
        List<Class<?>> list = ClassUtil.getClassList(packageName, true);
        if(CollectionUtils.isNotEmpty(list)){
            list = list.stream().filter(vo->vo.isAnnotationPresent(HandlerTag.class)).collect(Collectors.toList());
            list.forEach(vo->{
                String path = vo.getAnnotation(com.skr.signal.front.anno.HandlerTag.class).path();
                Handler handler;
                try {
                    handler = (Handler)vo.newInstance();
                } catch (Exception e) {
                    return;
                }
                if(!Objects.isNull(urlHandlerMapping.get(path))){
                    throw new ServiceException("处理器路径存在冲突："+path);
                }
                urlHandlerMapping.put(path,handler);
            });
        }
    }

    public static Handler getHandler(String url){
       return urlHandlerMapping.get(url);
    }


}
