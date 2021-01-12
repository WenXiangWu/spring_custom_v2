package com.reign.spring.framework.webmvc.servlet;

import com.reign.spring.framework.annotation.*;
import com.reign.spring.framework.context.ApplicationContext;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @ClassName DispatchServlet
 * @Description servlet;职责：任务调度和请求分发
 * @Author wuwenxiang
 * @Date 2021-01-11 20:46
 * @Version 1.0
 **/
public class DispatcherServlet extends HttpServlet {

    //保存application.properties配置文件中的内容；
    private Properties contextConfig = new Properties();


    //存储所有扫描到的class
    private List<String> classList = new ArrayList<String>();

    //IOC容器；
    private Map<String, Object> iocMap = new HashMap<String, Object>();

    //handlerMapping
    private Map<String, Method> handlerMappingMap = new HashMap<String, Method>();

    private List<Handler> handlerList = new ArrayList<Handler>();

    private ApplicationContext applicationContext;


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            doDispatch1(req, resp);
        } catch (Exception e) {
            resp.getWriter().write("500 Exception " + Arrays.toString(e.getStackTrace()));
        }

    }

    private Handler getHandler(HttpServletRequest req) {
        if (handlerList.isEmpty()) {
            return null;
        }
        //绝对路径
        String url = req.getRequestURI();
        //处理成相对路径
        String contextPath = req.getContextPath();
        url = url.replaceAll(contextPath, "").replaceAll("/+", "/");


        for (Handler handler : this.handlerList) {
            Matcher matcher = handler.getPattern().matcher(url);
            if (!matcher.matches()) {
                continue;
            }
            return handler;
        }
        return null;
    }

    private void doDispatch1(HttpServletRequest req, HttpServletResponse resp) throws Exception {

        Handler handler = getHandler(req);
        if (handler == null) {
//        if(!this.handlerMapping.containsKey(url)){
            resp.getWriter().write("404 Not Found!!!");
            return;
        }

        //获得方法的形参列表
        Class<?>[] paramTypes = handler.getParamTypes();

        Object[] paramValues = new Object[paramTypes.length];

        Map<String, String[]> params = req.getParameterMap();
        for (Map.Entry<String, String[]> parm : params.entrySet()) {
            String value = Arrays.toString(parm.getValue()).replaceAll("\\[|\\]", "")
                    .replaceAll("\\s", ",");

            if (!handler.paramIndexMapping.containsKey(parm.getKey())) {
                continue;
            }

            int index = handler.paramIndexMapping.get(parm.getKey());
            paramValues[index] = convert(paramTypes[index], value);
        }

        if (handler.paramIndexMapping.containsKey(HttpServletRequest.class.getName())) {
            int reqIndex = handler.paramIndexMapping.get(HttpServletRequest.class.getName());
            paramValues[reqIndex] = req;
        }

        if (handler.paramIndexMapping.containsKey(HttpServletResponse.class.getName())) {
            int respIndex = handler.paramIndexMapping.get(HttpServletResponse.class.getName());
            paramValues[respIndex] = resp;
        }

        Object returnValue = handler.method.invoke(handler.controller, paramValues);
        if (returnValue == null || returnValue instanceof Void) {
            return;
        }
        resp.getWriter().write(returnValue.toString());
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


    /**
     * 初始化Spring 相关
     *
     * @param config
     * @throws ServletException
     */
    @Override
    public void init(ServletConfig config) throws ServletException {
        System.out.println("初始化IOC容器");
        applicationContext = new ApplicationContext(config.getInitParameter("contextConfigLocation"));
        //4.初始化HandlerMapping，将URL与对应处理的Method进行绑定；
        initHandlerMapping();

    }

    //初始化url和Method的一对一对应关系
    private void initHandlerMapping() {
        if (applicationContext.getBeanDefinitionCount() == 0) {
            return;
        }
        for (String beanName : applicationContext.getBeanDefinitionNames()) {
            //接口不可实例化
            Object instance = applicationContext.getBean(beanName);
            if (instance == null) continue;
            Class<?> clazz = instance.getClass();
            if (!clazz.isAnnotationPresent(Controller.class)) {
                continue;
            }
            //保存写在类上面的@GPRequestMapping("/demo")
            String baseUrl = "";
            if (clazz.isAnnotationPresent(RequestMapping.class)) {
                RequestMapping requestMapping = clazz.getAnnotation(RequestMapping.class);
                baseUrl = requestMapping.value();
            }

            //默认获取所有的public方法
            for (Method method : clazz.getDeclaredMethods()) {
                if (!method.isAnnotationPresent(RequestMapping.class)) {
                    continue;
                }

                RequestMapping requestMapping = method.getAnnotation(RequestMapping.class);
                //优化
                // //demo///query
                String regex = ("/" + baseUrl + "/" + requestMapping.value())
                        .replaceAll("/+", "/");
                Pattern pattern = Pattern.compile(regex);
                this.handlerList.add(new Handler(pattern, applicationContext.getBean(beanName), method));
//                handlerMapping.put(url,method);
                System.out.println("Mapped :" + pattern + "," + method);

            }
        }


    }

    /**
     * 初始化HandlerMapping;
     */
    private void doInitHandlerMapping() {
        for (Map.Entry<String, Object> entry : iocMap.entrySet()) {
            Object instance = entry.getValue();
            //只会处理Controller
            String baseUrl = "";
            if (!instance.getClass().isAnnotationPresent(Controller.class)) continue;
            boolean existClass = instance.getClass().isAnnotationPresent(RequestMapping.class);
            if (existClass) {
                RequestMapping classRequestMapping = instance.getClass().getAnnotation(RequestMapping.class);
                //类上面的url
                baseUrl += classRequestMapping.value();
            }
            //仅处理public的方法，private方法不会处理
            for (Method method : instance.getClass().getMethods()) {
                boolean existMapping = method.isAnnotationPresent(RequestMapping.class);
                if (!existMapping) continue;
                RequestMapping methodRequestMapping = method.getAnnotation(RequestMapping.class);
                baseUrl += methodRequestMapping.value();
                baseUrl.replaceAll("/+", "/");
                if (!handlerMappingMap.containsKey(baseUrl)) {
                    handlerMappingMap.put(baseUrl, method);
                }
            }
        }
    }


    //保存一个url和一个Method的关系
    public class Handler {
        //必须把url放到HandlerMapping才好理解吧
        private Pattern pattern;  //正则
        private Method method;
        private Object controller;
        private Class<?>[] paramTypes;

        public Pattern getPattern() {
            return pattern;
        }

        public Method getMethod() {
            return method;
        }

        public Object getController() {
            return controller;
        }

        public Class<?>[] getParamTypes() {
            return paramTypes;
        }

        //形参列表
        //参数的名字作为key,参数的顺序，位置作为值
        private Map<String, Integer> paramIndexMapping;

        public Handler(Pattern pattern, Object controller, Method method) {
            this.pattern = pattern;
            this.method = method;
            this.controller = controller;

            paramTypes = method.getParameterTypes();

            paramIndexMapping = new HashMap<String, Integer>();
            putParamIndexMapping(method);
        }

        private void putParamIndexMapping(Method method) {

            //提取方法中加了注解的参数
            //把方法上的注解拿到，得到的是一个二维数组
            //因为一个参数可以有多个注解，而一个方法又有多个参数
            Annotation[][] pa = method.getParameterAnnotations();
            for (int i = 0; i < pa.length; i++) {
                for (Annotation a : pa[i]) {
                    if (a instanceof RequestParam) {
                        String paramName = ((RequestParam) a).value();
                        if (!"".equals(paramName.trim())) {
                            paramIndexMapping.put(paramName, i);
                        }
                    }
                }
            }

            //提取方法中的request和response参数
            Class<?>[] paramsTypes = method.getParameterTypes();
            for (int i = 0; i < paramsTypes.length; i++) {
                Class<?> type = paramsTypes[i];
                if (type == HttpServletRequest.class ||
                        type == HttpServletResponse.class) {
                    paramIndexMapping.put(type.getName(), i);
                }
            }

        }
    }
}
