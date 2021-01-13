package com.reign.spring.framework.context;

import com.reign.spring.framework.annotation.Autowired;
import com.reign.spring.framework.annotation.Controller;
import com.reign.spring.framework.annotation.Service;
import com.reign.spring.framework.aop.config.AopConfig;
import com.reign.spring.framework.aop.proxy.JdkDynamicAopProxy;
import com.reign.spring.framework.aop.support.AdvisedSupport;
import com.reign.spring.framework.beans.BeanWrapper;
import com.reign.spring.framework.beans.config.BeanDefinition;
import com.reign.spring.framework.beans.support.BeanDefinitionReader;

import java.lang.reflect.Field;
import java.util.*;

/**
 * @ClassName ApplicationContext
 * @Description spring上下文；职责为IOC容器及DI
 * @Author wuwenxiang
 * @Date 2021-01-11 21:15
 * @Version 1.0
 **/
public class ApplicationContext {

    private BeanDefinitionReader reader;

    private Map<String, BeanDefinition> beanDefinitionMap = new HashMap<String, BeanDefinition>();

    //IOC容器
    private Map<String, BeanWrapper> factoryBeanInstanceCache = new HashMap<String, BeanWrapper>();

    //保存原始对象
    private Map<String, Object> factoryBeanObjectCache = new HashMap<String, Object>();

    //TODO 双缓存解决循环依赖问题 key:field名称 value：尚未组装好的BeanWrapper
    private Map<String, Set<BeanWrapper>> firstCache = new HashMap<String, Set<BeanWrapper>>();

    /**
     * 通过配置文件进行初始化
     *
     * @param configLocations
     */
    public ApplicationContext(String... configLocations) {
        //1.读取配置文件
        reader = new BeanDefinitionReader(configLocations);
        //2.解析配置文件，封装成beanDefinition
        List<BeanDefinition> beanDefinitionList = reader.loadBeanDifinitions();
        //3.缓存BeanDefinition
        doRegisterBeanDefinition(beanDefinitionList);
        //4.依赖注入，仅限于非延时加载；
        doAutowired();
    }

    private void doAutowired() {
    }

    private void doRegisterBeanDefinition(List<BeanDefinition> beanDefinitionList) {
        for (BeanDefinition beanDefinition : beanDefinitionList) {
            if (beanDefinitionMap.containsKey(beanDefinition.getFactoryBeanName())) continue;
            //throw new IllegalArgumentException("The bean " + beanDefinition.getFactoryBeanName() + " already exist");
            beanDefinitionMap.put(beanDefinition.getFactoryBeanName(), beanDefinition);
            beanDefinitionMap.put(beanDefinition.getBeanClassName(), beanDefinition);
        }
    }

    /**
     * bean的实例化，DI是从这个方法开始的
     *
     * @param beanName
     * @return
     */
    public Object getBean(String beanName) {
        Object result = null;
        BeanWrapper beanWrapper = factoryBeanInstanceCache.get(beanName);
        if (beanWrapper != null) {
            result = beanWrapper.getWrapperInstance();
        } else {
            //1.先拿到BeanDefinition配置信息
            BeanDefinition beanDefinition = this.beanDefinitionMap.get(beanName);
            result = factoryBeanObjectCache.get(beanName);
            if (result == null) {
                //2,反射实例化
                result = instantiateBean(beanName, beanDefinition);
                //3.构造wrapper对象
                beanWrapper = new BeanWrapper(result);
                //4.保存到IOC容器
                factoryBeanInstanceCache.put(beanName, beanWrapper);
                //5.DI
                populateBean(beanName, beanDefinition, beanWrapper);
            }
        }
        return result;
    }

    /**
     * 执行依赖注入； TODO 需要处理循环依赖问题
     *
     * @param beanName
     * @param beanDefinition
     * @param beanWrapper
     */
    private void populateBean(String beanName, BeanDefinition beanDefinition, BeanWrapper beanWrapper) {
        //解决循环依赖；用两级缓存，循环两次就可以解决；
        //1.把第一次读取结果为空的BeanDefinition存到第一个缓存；

        //2.等第一次循环后，第二次循环再检查第一次循环之后结果为空的再进行赋值；

        Object instance = beanWrapper.getWrapperInstance();
        Class<?> clazz = beanWrapper.getWrappedClass();
        //TODO Component注解，是他们的父类
        if (!(clazz.isAnnotationPresent(Controller.class) || clazz.isAnnotationPresent(Service.class))) return;
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                boolean isAutowired = field.isAnnotationPresent(Autowired.class);
                if (!isAutowired) continue;
                Autowired autowired = field.getAnnotation(Autowired.class);
                //这边默认注入的参数首字母小写 FIXME
                String autowiredBeanName = autowired.value().trim();
                if (autowiredBeanName.equals("")) autowiredBeanName = field.getName();
                if (factoryBeanInstanceCache.get(autowiredBeanName) == null) {
                    Set<BeanWrapper> obj = null;
                    //直接存储全路径名
                    String interfaceName = toLowerFirstCase(field.getType().getSimpleName());
                    if (!firstCache.containsKey(interfaceName)) {
                        obj = new HashSet<BeanWrapper>();
                    } else {
                        obj = firstCache.get(interfaceName);
                    }
                    obj.add(beanWrapper);
                    firstCache.put(interfaceName, obj);
                    continue;
                }
                field.set(instance, factoryBeanInstanceCache.get(autowiredBeanName).getWrapperInstance());
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                continue;
            }
        }
    }
    public String toLowerFirstCase(String className) {
        char[] chars = className.toCharArray();
        chars[0] += 32;
        return String.valueOf(chars);
    }
    /**
     * 创建真正的实例对象，没有做DI
     *
     * @param beanName
     * @param beanDefinition
     * @return
     */
    private Object instantiateBean(String beanName, BeanDefinition beanDefinition) {
        Object instance = factoryBeanObjectCache.get(beanName);
        try {
            if (instance == null) {
                Class<?> clazz = Class.forName(beanDefinition.getBeanClassName());
                instance = clazz.newInstance();
                //TODO 简单做aop
                AdvisedSupport advisedSupport = instantionAopConfig(beanDefinition);
                advisedSupport.setTarget(instance);
                advisedSupport.setTargetClass(clazz);
                //判断规则是否要生成代理类，如果要则覆盖原生对象
                if(advisedSupport.pointCutMath()){
                    instance = new JdkDynamicAopProxy(advisedSupport).getProxy();
                }
                //TODO aop结束
                factoryBeanObjectCache.put(beanName, instance);
            }
            //实例化好之后去填充之前循环依赖没有的对象；
            Set<BeanWrapper> beanWrappers = firstCache.get(beanDefinition.getFactoryBeanName());
            if (beanWrappers != null && beanWrappers.size() > 0) {
                for (BeanWrapper beanWrapper : beanWrappers) {
                    Object o = beanWrapper.getWrapperInstance();
                    for (Field field : o.getClass().getDeclaredFields()) {
                        if (field.getName().equals(beanDefinition.getFactoryBeanName())) {
                            field.setAccessible(true);
                            field.set(o, instance);
                        }
                    }
                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
        return instance;
    }

    private AdvisedSupport instantionAopConfig(BeanDefinition beanDefinition) {
        AopConfig aopConfig = new AopConfig();
        aopConfig.setPointCut(this.reader.getConfigProperties().getProperty("pointCut"));
        aopConfig.setAspectClass(this.reader.getConfigProperties().getProperty("aspectClass"));
        aopConfig.setAspectBefore(this.reader.getConfigProperties().getProperty("aspectBefore"));
        aopConfig.setAspectAfter(this.reader.getConfigProperties().getProperty("aspectAfter"));
        aopConfig.setAspectAfterThrow(this.reader.getConfigProperties().getProperty("aspectAfterThrow"));
        aopConfig.setAspectAfterThrowingName(this.reader.getConfigProperties().getProperty("aspectAfterThrowingName"));
        return new AdvisedSupport(aopConfig);
    }

    public Object getBean(Class clazz) {
        return getBean(clazz.getName());
    }

    public int getBeanDefinitionCount() {
        return beanDefinitionMap.size();
    }

    public Set<String> getBeanDefinitionNames() {
        return this.beanDefinitionMap.keySet();
    }


    public Properties getConfig() {
        return this.reader.getConfigProperties();
    }
}
