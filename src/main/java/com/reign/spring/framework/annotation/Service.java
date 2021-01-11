package com.reign.spring.framework.annotation;

import java.lang.annotation.*;

/**
 * @ClassName Service
 * @Description Service注解
 * @Author wuwenxiang
 * @Date 2021-01-07 20:58
 * @Version 1.0
 **/
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface Service {
    String value() default "";
}
