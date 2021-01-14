package com.reign.demo.dynamicproxy.jdk;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @ClassName JdkLogProxy
 * @Description TODO
 * @Author wuwenxiang
 * @Date 2021-01-13 19:54
 * @Version 1.0
 **/
public class JdkLogProxy implements InvocationHandler {

    private Object target;

    public Object getTarget() {
        return target;
    }

    public void setTarget(Object target) {
        this.target = target;
    }

    public JdkLogProxy(Object target) {
        this.target = target;
    }

    public JdkLogProxy() {
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object result = null;
        System.out.println("代理开始");
        result = method.invoke(target,args);
        System.out.println(result);
        System.out.println("代理结束");
        return result;
    }

    public Object getInstance() {
        Class<?> clazz = target.getClass();
        return  Proxy.newProxyInstance(clazz.getClassLoader(), clazz.getInterfaces(), this);
    }
}
