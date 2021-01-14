package com.reign.demo.dynamicproxy.cglib;

import com.reign.demo.service.impl.OrderServiceImpl;
import net.sf.cglib.core.DebuggingClassWriter;

/**
 * @ClassName TestCglibProxy
 * @Description 测试Cglib
 * @Author wuwenxiang
 * @Date 2021-01-13 22:34
 * @Version 1.0
 **/
public class TestCglibProxy {
    public static void main(String[] args) {
        //将新生成的class文件写入到文件中
        System.setProperty(DebuggingClassWriter.DEBUG_LOCATION_PROPERTY,"F:\\视频教程\\gupaoedu-vip-spring-1.0\\spring_custom_v2\\src\\main\\java\\com\\reign\\demo\\dynamicproxy\\cglib\\cglib_generate_classes");
        OrderServiceImpl orderService = (OrderServiceImpl) new CglibProxy().getInstance(OrderServiceImpl.class);
        orderService.getOrderInfo("1");

    }
}
