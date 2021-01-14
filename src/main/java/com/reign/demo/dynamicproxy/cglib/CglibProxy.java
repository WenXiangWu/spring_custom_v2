package com.reign.demo.dynamicproxy.cglib;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

/**
 * @ClassName CglibProxy
 * @Description cglib代理
 * @Author wuwenxiang
 * @Date 2021-01-13 19:52
 * @Version 1.0
 **/
public class CglibProxy implements MethodInterceptor {

    public Object getInstance(Class<?> clazz) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(clazz);
        enhancer.setCallback(this);
        return enhancer.create();
    }

    /**
     *
     * @param o  生成的代理对象的实例
     * @param method  原对象的方法
     * @param objects 方法参数
     * @param methodProxy 生成的代理对象实例的方法
     * @return
     * @throws Throwable
     */
    @Override
    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
        before();
        Object obj = methodProxy.invokeSuper(o, objects);
        after();
        return obj;
    }

    public void before() {
        System.out.println("cglib代理开始");
    }

    public void after() {
        System.out.println("cglib代理结束");
    }


}
