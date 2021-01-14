package com.reign.demo.dynamicproxy.jdk;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @ClassName JdkLogProxyPro
 * @Description TODO
 * @Author wuwenxiang
 * @Date 2021-01-14 9:46
 * @Version 1.0
 **/
public class JdkLogProxyPro implements InvocationHandler {

    private Object target;

    public JdkLogProxyPro(Object target) {
        this.target = target;
    }

    public Object getTarget() {
        return target;
    }

    public void setTarget(Object target) {
        this.target = target;
    }

    public Object getInstance(){
        Class<?> clazz = target.getClass();
        return Proxy.newProxyInstance(clazz.getClassLoader(),clazz.getInterfaces(),this);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        before();
        Object result = method.invoke(target,args);
        System.out.println(result);
        after();
        return result;
    }

    public JdkLogProxyPro() {
    }

    private void before() {
        System.out.println("前面");
    }

    private void after() {
        System.out.println("后面");
    }


}
