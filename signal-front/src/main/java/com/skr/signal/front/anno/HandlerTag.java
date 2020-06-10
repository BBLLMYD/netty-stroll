package com.skr.signal.front.anno;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author mqw
 * @create 2020-06-04-11:03 下午
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE,ElementType.LOCAL_VARIABLE})
public @interface HandlerTag {

    String path() default "";

}
