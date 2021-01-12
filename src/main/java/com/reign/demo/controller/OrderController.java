package com.reign.demo.controller;



import com.reign.demo.service.OrderService;
import com.reign.spring.framework.annotation.Autowired;
import com.reign.spring.framework.annotation.Controller;
import com.reign.spring.framework.annotation.RequestMapping;
import com.reign.spring.framework.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * @ClassName OrderController
 * @Description 订单服务
 * @Author wuwenxiang
 * @Date 2021-01-07 22:02
 * @Version 1.0
 **/
@Controller
@RequestMapping("/orderController")
public class OrderController {


    @Autowired
    private OrderService orderService;

    @RequestMapping(value = "/getOrderInfo")
    public String  getOrderInfo(HttpServletRequest req, HttpServletResponse resp,@RequestParam("orderId") String  orderId) {
        System.out.println("进入Controller");
        return orderService.getOrderInfo(orderId);
    }

}
