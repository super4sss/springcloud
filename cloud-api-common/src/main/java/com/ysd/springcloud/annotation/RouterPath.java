package com.ysd.springcloud.annotation;

import java.lang.annotation.*;

/**
 * 定义Jfinal Controller路由
 * @author chenmin
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface RouterPath {
    /**
     * 路由值
     * @return
     */
    String value() default "";
}
