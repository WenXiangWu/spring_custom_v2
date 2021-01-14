package com.reign.demo.domain;

import java.util.Date;

/**
 * @ClassName Order
 * @Description 订单信息
 * @Author wuwenxiang
 * @Date 2021-01-07 22:06
 * @Version 1.0
 **/
public class Order {

    private int orderId;

    private Date create_time;

    private double price;

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public Date getCreate_time() {
        return create_time;
    }

    public void setCreate_time(Date create_time) {
        this.create_time = create_time;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public Order(int orderId, Date create_time, double price) {
        this.orderId = orderId;
        this.create_time = create_time;
        this.price = price;
    }

    public Order() {
    }

    @Override
    public String toString() {
        return "Order{" +
                "orderId=" + orderId +
                ", create_time=" + create_time +
                ", price=" + price +
                '}';
    }
}
