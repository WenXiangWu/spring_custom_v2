package com.reign.demo.dynamicproxy.jdk;

import com.reign.demo.dynamicproxy.jdk.JdkLogProxy;
import com.reign.demo.service.OrderService;
import com.reign.demo.service.impl.OrderServiceImpl;
import sun.misc.ProxyGenerator;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @ClassName TestJdkProxy
 * @Description 测试类;
 * @Author wuwenxiang
 * @Date 2021-01-13 19:55
 * @Version 1.0
 **/
public class TestJdkProxy {
    public static void main(String[] args) {
        OrderService orderService = new OrderServiceImpl();
        System.out.println(orderService.getOrderInfo("1"));

        //执行代理
        JdkLogProxyPro jdkLogProxy = new JdkLogProxyPro(orderService);
        OrderService orderService1 = (OrderService) jdkLogProxy.getInstance();
        orderService1.getOrderInfo("1");

        //将新生成的class写入到文件中
        byte[] bytes = ProxyGenerator.generateProxyClass("$Proxy0",new Class[]{OrderService.class});
        FileOutputStream os = null;
        try {
            os = new FileOutputStream("F:\\视频教程\\gupaoedu-vip-spring-1.0\\spring_custom_v2\\src\\main\\java\\com\\reign\\demo\\dynamicproxy\\jdk\\jdkproxy_generate_classes\\$Proxy0.class");
            os.write(bytes);
            os.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
