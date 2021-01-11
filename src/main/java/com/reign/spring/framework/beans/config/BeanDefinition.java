package com.reign.spring.framework.beans.config;

/**
 * @ClassName BeanDefinition
 * @Description 类定义
 * @Author wuwenxiang
 * @Date 2021-01-11 21:16
 * @Version 1.0
 **/
public class BeanDefinition {
    //在IOC容器中的名字
    private String factoryBeanName;
    //类实际的名字
    private String beanClassName;

    public String getFactoryBeanName() {
        return factoryBeanName;
    }

    public void setFactoryBeanName(String factoryBeanName) {
        this.factoryBeanName = factoryBeanName;
    }

    public String getBeanClassName() {
        return beanClassName;
    }

    public void setBeanClassName(String beanClassName) {
        this.beanClassName = beanClassName;
    }
}
