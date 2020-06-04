package com.ysd.springcloud.common.auth;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 菜单注解，作用在Controller和Controller的方法上，
 * 用于页面的菜单和权限控制
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Menu {
    String value();
    MenuType type() default MenuType.MENU;
    String[] replace() default {};
}
