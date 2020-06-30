package com.skr.signal.base.rpc.server.anno;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author mqw
 * @create 2020-06-25-08:47
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE,ElementType.LOCAL_VARIABLE})
public @interface RpcServiceTag {

    // 发布的接口
    Class targetService();

    String serviceTag() default "";

}
