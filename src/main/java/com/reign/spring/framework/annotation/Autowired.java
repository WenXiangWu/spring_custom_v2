package com.reign.spring.framework.annotation;

import java.lang.annotation.*;

/**
 * @ClassName Autowired
 * @Description TODO
 * @Author wuwenxiang
 * @Date 2021-01-07 20:45
 * @Version 1.0
 **/
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Documented
public @interface Autowired {
    String value() default "";
}
