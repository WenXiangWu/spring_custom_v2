package com.reign.spring.framework.webmvc.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Map;

/**
 * @ClassName HandlerAdapter
 * @Description handler适配器
 * @Author wuwenxiang
 * @Date 2021-01-12 21:04
 * @Version 1.0
 **/
public class HandlerAdapter {


    public ModelAndView handler(HttpServletRequest req, HttpServletResponse resp,HandlerMapping handler) {
        //获得方法的形参列表
        Class<?>[] paramTypes = handler.getMethod().getParameterTypes();

        Object[] paramValues = new Object[paramTypes.length];

        Map<String, String[]> params = req.getParameterMap();
        for (Map.Entry<String, String[]> parm : params.entrySet()) {
            String value = Arrays.toString(parm.getValue()).replaceAll("\\[|\\]", "")
                    .replaceAll("\\s", ",");

            if (!handler.getParamIndexMapping().containsKey(parm.getKey())) {
                continue;
            }

            int index = handler.getParamIndexMapping().get(parm.getKey());
            paramValues[index] = convert(paramTypes[index], value);
        }

        if (handler.getParamIndexMapping().containsKey(HttpServletRequest.class.getName())) {
            int reqIndex = handler.getParamIndexMapping().get(HttpServletRequest.class.getName());
            paramValues[reqIndex] = req;
        }

        if (handler.getParamIndexMapping().containsKey(HttpServletResponse.class.getName())) {
            int respIndex = handler.getParamIndexMapping().get(HttpServletResponse.class.getName());
            paramValues[respIndex] = resp;
        }

        try {
            Object returnValue = handler.getMethod().invoke(handler.getController(), paramValues);
            if (returnValue == null || returnValue instanceof Void) return null;
            boolean isModelAndView = handler.getMethod().getReturnType() == ModelAndView.class;
            if(isModelAndView){
                return (ModelAndView)returnValue;
            }
            return null;

        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    //url传过来的参数都是String类型的，HTTP是基于字符串协议
    //只需要把String转换为任意类型就好
    private Object convert(Class<?> type, String value) {
        //如果是int
        if (Integer.class == type) {
            return Integer.valueOf(value);
        } else if (Double.class == type) {
            return Double.valueOf(value);
        }
        //如果还有double或者其他类型，继续加if
        //这时候，我们应该想到策略模式了
        //在这里暂时不实现，希望小伙伴自己来实现
        return value;
    }

}
