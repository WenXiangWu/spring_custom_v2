package com.reign.spring.framework.annotation;

import java.lang.annotation.*;

/**
 * @ClassName Bean
 * @Description bean注解
 * @Author wuwenxiang
 * @Date 2021-01-07 21:00
 * @Version 1.0
 **/
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface Bean {
}
