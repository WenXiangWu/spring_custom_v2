package com.reign.demo.aspect;

/**
 * @ClassName: LogAspect
 * @Description: 切面
 * @Author: wuwx
 * @Date: 2021-01-13 15:00
 **/
public class LogAspect {

    //在调用一个方法之前，执行before方法
    public void before(){
        //这个方法中的逻辑，是由我们自己写的
        System.out.println("Invoker Before Method!!!");
    }
    //在调用一个方法之后，执行after方法
    public void after(){
        System.out.println("Invoker After Method!!!");
    }

    public void afterThrowing(){
        System.out.println("出现异常");
    }

}
