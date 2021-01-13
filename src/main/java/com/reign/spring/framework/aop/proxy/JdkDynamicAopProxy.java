package com.reign.spring.framework.aop.proxy;

import com.reign.spring.framework.aop.aspect.Advice;
import com.reign.spring.framework.aop.support.AdvisedSupport;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;

/**
 * @ClassName: JdkDynamicAopProxy
 * @Description: TODO
 * @Author: wuwx
 * @Date: 2021-01-13 15:01
 **/
public class JdkDynamicAopProxy implements InvocationHandler {

    private AdvisedSupport config;

    public JdkDynamicAopProxy(AdvisedSupport config) {
        this.config = config;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Map<String, Advice> advices = config.getAdvices(method,null);

        Object returnValue;
        try {
            invokeAdivce(advices.get("before"));

            returnValue = method.invoke(this.config.getTarget(),args);

            invokeAdivce(advices.get("after"));
        }catch (Exception e){
            invokeAdivce(advices.get("afterThrow"));
            throw e;
        }

        return returnValue;
    }

    private void invokeAdivce(Advice advice) {
        try {
            advice.getAdviceMethod().invoke(advice.getAspect());
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public Object getProxy() {
        return Proxy.newProxyInstance(this.getClass().getClassLoader(),this.config.getTargetClass().getInterfaces(),this);
    }

}
