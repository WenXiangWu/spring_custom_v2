package com.reign.spring.framework.aop.proxy;

/**
 * @ClassName AopProxyEnum
 * @Description 公共类
 * @Author wuwenxiang
 * @Date 2021-01-14 14:37
 * @Version 1.0
 **/
public enum AopProxyEnum {
    JDK_PROXY("jdk", JdkDynamicAopProxy.class),

    CGLIB_PROXY("cglib", CglibAopProxy.class);

    private String proxyType;

    private Class<?> clazz;

    AopProxyEnum(String proxyType, Class<?> clazz) {
        this.proxyType = proxyType;
        this.clazz = clazz;
    }

    /**
     * AOP默认采用Cglib
     *
     * @param proxyType
     * @return
     */
    public static Class getClassByProxyType(String proxyType) {
        for (AopProxyEnum item : AopProxyEnum.values()) {
            if (proxyType.equalsIgnoreCase(item.proxyType)) return item.clazz;
        }
        return CglibAopProxy.class;
    }

}
