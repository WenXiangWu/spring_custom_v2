package com.reign.spring.framework.annotation;

import java.lang.annotation.*;

/**
 * @ClassName ComponentScan
 * @Description ComponentScan注解
 * @Author wuwenxiang
 * @Date 2021-01-07 20:59
 * @Version 1.0
 **/
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface ComponentScan {
}
