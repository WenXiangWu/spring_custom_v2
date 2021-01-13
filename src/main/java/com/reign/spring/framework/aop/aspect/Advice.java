package com.reign.spring.framework.aop.aspect;

import java.lang.reflect.Method;

/**
 * @ClassName: Advice
 * @Description: 通知
 * @Author: wuwx
 * @Date: 2021-01-13 15:00
 **/
public class Advice {
    private Object aspect;
    private Method adviceMethod;
    private String throwName;

    public Advice(Object aspect, Method adviceMethod) {
        this.aspect = aspect;
        this.adviceMethod = adviceMethod;
    }

    public Object getAspect() {
        return aspect;
    }

    public void setAspect(Object aspect) {
        this.aspect = aspect;
    }

    public Method getAdviceMethod() {
        return adviceMethod;
    }

    public void setAdviceMethod(Method adviceMethod) {
        this.adviceMethod = adviceMethod;
    }

    public String getThrowName() {
        return throwName;
    }

    public void setThrowName(String throwName) {
        this.throwName = throwName;
    }
}
