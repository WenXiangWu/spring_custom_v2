package com.reign.demo.aspect;

/**
 * @ClassName: MethodExecuteTimeAspect
 * @Description: 方法调用时间统计
 * @Author: wuwx
 * @Date: 2021-01-13 16:58
 **/
public class MethodExecuteTimeAspect {
    //增加一个field用来统计方法耗时

    private long timeConsuming;

    public void start() {
        timeConsuming = System.currentTimeMillis();
    }

    public void end() {
        System.out.println(System.currentTimeMillis() - timeConsuming);
    }


}
