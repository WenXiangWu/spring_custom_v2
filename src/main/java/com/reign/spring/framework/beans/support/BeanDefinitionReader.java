package com.reign.spring.framework.beans.support;

import com.reign.spring.framework.beans.config.BeanDefinition;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * @ClassName BeanDefinitionReader
 * @Description 读取配置文件工具
 * @Author wuwenxiang
 * @Date 2021-01-11 21:16
 * @Version 1.0
 **/
public class BeanDefinitionReader {

    //配置文件位置
    private String[] configLocations;
    //配置信息
    Properties configProperties;
    //存储所有扫描到的class
    private List<String> registryBeanClasses = new ArrayList<String>();

    public BeanDefinitionReader(String[] configLocations) {
        this.configLocations = configLocations;
        this.configProperties = new Properties();
        //读取配置文件，这里假设只有一个配置文件
        doLoadConfig(configLocations[0]);
        //扫描配置的路径下的类
        doScan(configProperties.getProperty("scanPackage"));
        //组装BeanDefinition
        loadBeanDifinitions();
    }

    private void doLoadConfig(String configLocation) {
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(configLocation.replaceAll("classpath:", ""));
        try {
            configProperties.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<BeanDefinition> loadBeanDifinitions() {
        List<BeanDefinition> result = new ArrayList<BeanDefinition>();
        for (String className : registryBeanClasses) {
            try {
                Class<?> clazz = Class.forName(className);
                //保存类对应的className（或全类名）
                String beanClassName = clazz.getName();
                // 1.默认首字母小写；
                String beanName = toLowerFirstCase(clazz.getSimpleName());
                result.add(doCreateBeanDefinition(beanName,beanClassName));
                //2.自定义的名字；

                // 3.接口注入
                for (Class<?> i:clazz.getInterfaces()){
                    result.add(doCreateBeanDefinition(i.getName(),clazz.getName()));
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    private BeanDefinition doCreateBeanDefinition(String beanName, String beanClassName) {
        BeanDefinition beanDefinition = new BeanDefinition();
        beanDefinition.setBeanClassName(beanClassName);
        beanDefinition.setFactoryBeanName(beanName);
        return beanDefinition;
    }


    /**
     * 利用小写字母比大写字母的asnic码小32的特性；
     *
     * @param className
     * @return
     */
    public String toLowerFirstCase(String className) {
        char[] chars = className.toCharArray();
        chars[0] += 32;
        return String.valueOf(chars);
    }

    private void doScan(String scanPackage) {
        //获取配置文件中要扫描的包路径；
        URL url = this.getClass().getClassLoader().getResource("/" + scanPackage.replaceAll("\\.", "/"));
        File classPath = new File(url.getFile());
        //获取配置文件中配置的扫描路径下的所有文件夹和文件；
        for (File file : classPath.listFiles()) {
            if (file.isDirectory()) {
                doScan(scanPackage + "." + file.getName());
            } else {
                //处理扫描class文件
                if (!file.getName().endsWith(".class")) continue;
                String className = scanPackage + "." + file.getName().replace(".class", "");
                registryBeanClasses.add(className);
            }
        }
    }
}
