package com.reign.spring.framework.webmvc.servlet;

import com.reign.spring.framework.annotation.*;
import com.reign.spring.framework.context.ApplicationContext;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.lang.reflect.Method;
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


    private List<HandlerMapping> handlerMappings = new ArrayList<HandlerMapping>();

    private ApplicationContext applicationContext;

    private Map<HandlerMapping, HandlerAdapter> handlerAdapterMap = new HashMap<HandlerMapping, HandlerAdapter>();


    private List<ViewResolver> viewResolvers = new ArrayList<ViewResolver>();

    /**
     * 初始化Spring 相关
     *
     * @param config
     * @throws ServletException
     */
    @Override
    public void init(ServletConfig config) {
        System.out.println("初始化IOC容器");
        applicationContext = new ApplicationContext(config.getInitParameter("contextConfigLocation"));
        initStrategies(applicationContext);

    }

    private void initStrategies(ApplicationContext context) {

        initHandlerMapping(context);

        initHandlerAdapters(context);

        initViewResolvers(context);

    }

    //初始化url和Method的一对一对应关系
    private void initHandlerMapping(ApplicationContext context) {
        if (applicationContext.getBeanDefinitionCount() == 0) {
            return;
        }
        for (String beanName : applicationContext.getBeanDefinitionNames()) {
            //接口不可实例化
            Object instance = applicationContext.getBean(beanName);
            if (instance == null) continue;
            Class<?> clazz = instance.getClass();
            if (!clazz.isAnnotationPresent(Controller.class)) continue;

            //保存写在类上面的@GPRequestMapping("/demo")
            String baseUrl = "";
            if (clazz.isAnnotationPresent(RequestMapping.class)) {
                RequestMapping requestMapping = clazz.getAnnotation(RequestMapping.class);
                baseUrl = requestMapping.value();
            }
            //默认获取所有的public方法
            for (Method method : clazz.getDeclaredMethods()) {
                if (!method.isAnnotationPresent(RequestMapping.class)) continue;
                RequestMapping requestMapping = method.getAnnotation(RequestMapping.class);
                //优化
                String regex = ("/" + baseUrl + "/" + requestMapping.value()).replaceAll("/+", "/");
                Pattern pattern = Pattern.compile(regex);
                this.handlerMappings.add(new HandlerMapping(pattern, instance, method));
                System.out.println("Mapped :" + pattern + "," + method);
            }
        }
    }

    private void initHandlerAdapters(ApplicationContext context) {
        for (HandlerMapping handlerMapping : handlerMappings) {
            this.handlerAdapterMap.put(handlerMapping, new HandlerAdapter());
        }
    }


    private void initViewResolvers(ApplicationContext context) {
        String templateRoot = context.getConfig().getProperty("templateRoot");
        String templateRootPath = this.getClass().getClassLoader().getResource(templateRoot).getFile();
        File templateRootDir = new File(templateRootPath);
        for (File file : templateRootDir.listFiles()) {
            this.viewResolvers.add(new ViewResolver(templateRoot));
        }
    }


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            doDispatch(req, resp);
        } catch (Exception e) {
            resp.getWriter().write("500 Exception " + Arrays.toString(e.getStackTrace()));
        }

    }

    private HandlerMapping getHandler(HttpServletRequest req) {
        if (handlerMappings.isEmpty()) {
            return null;
        }
        //绝对路径
        String url = req.getRequestURI();
        //处理成相对路径
        String contextPath = req.getContextPath();
        url = url.replaceAll(contextPath, "").replaceAll("/+", "/");


        for (HandlerMapping handler : this.handlerMappings) {
            Matcher matcher = handler.getPattern().matcher(url);
            if (!matcher.matches()) {
                continue;
            }
            return handler;
        }
        return null;
    }

    private void doDispatch(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        //1,通过url获取一个handlerMapping
        HandlerMapping handler = getHandler(req);
        if (handler == null) {
            processDispatchResult(req, resp, new ModelAndView("404.html"));
            return;
        }
        //2.根据HandlerMapping获取一个HandlerAdapter
        HandlerAdapter handlerAdapter = getHandlerAdapter(handler);


        //3.根据适配器去解析某个一方法的形参和返回值之后，统一封装为ModelAndView对象
        ModelAndView modelAndView = handlerAdapter.handler(req, resp, handler);


        //4.把ModelAndView变为ViewResolver
        processDispatchResult(req, resp, modelAndView);

    }

    private HandlerAdapter getHandlerAdapter(HandlerMapping handler) {
        return handlerAdapterMap.get(handler);
    }

    private void processDispatchResult(HttpServletRequest req, HttpServletResponse resp, ModelAndView modelAndView) {
        if (null == modelAndView) {
            return;
        }
        if (this.viewResolvers.isEmpty()|| modelAndView.getViewName() == null || modelAndView.getViewName().equals("")) {
            try {
                Map<String,?> model = modelAndView.getModel();
                StringBuilder sb = new StringBuilder();
                sb.append("result:");

                for (Map.Entry<String,?> entry:model.entrySet()){
                    sb.append(entry.getKey()).append("_").append(entry.getValue());
                }
                resp.getWriter().write(sb.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }

        for (ViewResolver viewResolver : this.viewResolvers) {
            View view = viewResolver.resolveViewName(modelAndView.getViewName());
            //直接往浏览器输出
            try {
                view.render(modelAndView.getModel(), req, resp);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return;
        }
    }


}
