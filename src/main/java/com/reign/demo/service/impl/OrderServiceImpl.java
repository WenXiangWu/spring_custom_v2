package com.reign.demo.service.impl;


import com.reign.demo.service.OrderService;
import com.reign.spring.framework.annotation.Service;

/**
 * @ClassName OrderServiceImpl
 * @Description 订单服务
 * @Author wuwenxiang
 * @Date 2021-01-07 22:02
 * @Version 1.0
 **/
@Service
public class OrderServiceImpl implements OrderService {
    @Override
    public String getOrderInfo(String  orderId) {
        System.out.println("进入service");
        return "order is:"+orderId;
    }
}
