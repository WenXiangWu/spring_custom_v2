package com.reign.spring.framework.beans;

/**
 * @ClassName BeanWrapper
 * @Description bean包装类，职责为维护原始实例和后生成的代理实例关系
 * @Author wuwenxiang
 * @Date 2021-01-11 21:16
 * @Version 1.0
 **/
public class BeanWrapper {

    private Object wrapperInstance;

    private Class<?> wrappedClass;

    public BeanWrapper(Object wrapperInstance) {
        this.wrapperInstance = wrapperInstance;
        this.wrappedClass = wrapperInstance.getClass();
    }

    public Object getWrapperInstance() {
        return wrapperInstance;
    }

    public void setWrapperInstance(Object wrapperInstance) {
        this.wrapperInstance = wrapperInstance;
    }

    public Class<?> getWrappedClass() {
        return wrappedClass;
    }

    public void setWrappedClass(Class<?> wrappedClass) {
        this.wrappedClass = wrappedClass;
    }
}
