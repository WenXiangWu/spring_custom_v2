package com.reign.spring.framework.annotation;

import java.lang.annotation.*;

/**
 * @ClassName RequestMapping
 * @Description 请求路径的注解
 * @Author wuwenxiang
 * @Date 2021-01-07 22:03
 * @Version 1.0
 **/
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE,ElementType.METHOD})
@Documented
public @interface RequestMapping {
    String value();
}
