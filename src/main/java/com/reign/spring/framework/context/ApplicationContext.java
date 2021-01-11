package com.reign.spring.framework.context;

import com.reign.spring.framework.annotation.Autowired;
import com.reign.spring.framework.annotation.Controller;
import com.reign.spring.framework.annotation.Service;
import com.reign.spring.framework.beans.BeanWrapper;
import com.reign.spring.framework.beans.config.BeanDefinition;
import com.reign.spring.framework.beans.support.BeanDefinitionReader;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        //调用getBean
        //这里所有的bean还没有真正的实例化，仍然是配置阶段
        for (Map.Entry<String, BeanDefinition> beanDefinitionEntry : beanDefinitionMap.entrySet()) {
            String beanName = beanDefinitionEntry.getKey();

        }
    }

    private void doRegisterBeanDefinition(List<BeanDefinition> beanDefinitionList) {
        for (BeanDefinition beanDefinition : beanDefinitionList) {
            if (beanDefinitionMap.containsKey(beanDefinition.getFactoryBeanName()))
                throw new IllegalArgumentException("The bean " + beanDefinition.getFactoryBeanName() + " already exist");
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
        //1.先拿到BeanDefinition配置信息
        BeanDefinition beanDefinition = this.beanDefinitionMap.get(beanName);
        //2,反射实例化
        Object instance = instantiateBean(beanName, beanDefinition);
        //3.构造wrapper对象
        BeanWrapper beanWrapper = new BeanWrapper(instance);
        //4.保存到IOC容器
        factoryBeanInstanceCache.put(beanName, beanWrapper);
        //5.DI
        populateBean(beanName, beanDefinition, beanWrapper);
        return beanWrapper.getWrapperInstance();
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
                if (beanName.equals("")) autowiredBeanName = field.getName();
                if (factoryBeanInstanceCache.get(autowiredBeanName) == null) continue;
                field.set(instance, factoryBeanInstanceCache.get(autowiredBeanName).getWrapperInstance());
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                continue;
            }
        }
    }

    /**
     * 创建真正的实例对象，没有做DI
     *
     * @param beanName
     * @param beanDefinition
     * @return
     */
    private Object instantiateBean(String beanName, BeanDefinition beanDefinition) {
        Object instance = null;
        try {
            Class<?> clazz = Class.forName(beanDefinition.getBeanClassName());
            instance = clazz.newInstance();
            factoryBeanObjectCache.put(beanName, instance);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
        return instance;
    }

    public Object getBean(Class clazz) {
        return getBean(clazz.getName());
    }

}