package com.reign.spring.framework.annotation;

import java.lang.annotation.*;

/**
 * @ClassName RequestParam
 * @Description 参数注解
 * @Author wuwenxiang
 * @Date 2021-01-11 16:58
 * @Version 1.0
 **/
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
@Documented
public @interface RequestParam {
    String value() default "";
}
