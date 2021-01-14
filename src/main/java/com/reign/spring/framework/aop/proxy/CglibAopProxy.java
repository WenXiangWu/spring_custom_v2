package com.reign.spring.framework.aop.proxy;

import com.reign.spring.framework.aop.aspect.Advice;
import com.reign.spring.framework.aop.support.AdvisedSupport;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * @ClassName: CglibAopProxy
 * @Description: 基于cglib的代理
 * @Author: wuwx
 * @Date: 2021-01-13 15:01
 **/
public class CglibAopProxy implements MethodInterceptor {

    private AdvisedSupport config;

    public CglibAopProxy(AdvisedSupport config) {
        this.config = config;
    }


    public AdvisedSupport getConfig() {
        return config;
    }

    public void setConfig(AdvisedSupport config) {
        this.config = config;
    }

    public Object getProxy(){
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(config.getTargetClass());
        enhancer.setCallback(this);
        return enhancer.create();
    }

    @Override
    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
        Map<String, Advice> advices = config.getAdvices(method,null);

        Object returnValue = null;
        try {
            invokeAdivce(advices.get("before"));
            returnValue = methodProxy.invokeSuper(o,objects);;
            //returnValue = method.invoke(this.config.getTarget(),args);
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
}
