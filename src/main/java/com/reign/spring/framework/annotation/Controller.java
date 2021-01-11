package com.reign.spring.framework.annotation;

import java.lang.annotation.*;

/**
 * @ClassName Controller
 * @Description controller注解
 * @Author wuwenxiang
 * @Date 2021-01-07 20:55
 * @Version 1.0
 **/
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface Controller {
    String value() default "";
}
