package com.reign.spring.framework.webmvc.servlet;

import java.util.Map;

/**
 * @ClassName ModelAndView
 * @Description modelAndView
 * @Author wuwenxiang
 * @Date 2021-01-12 21:04
 * @Version 1.0
 **/
public class ModelAndView {

    private String viewName;

    private Map<String ,?> model;

    public ModelAndView(String viewName, Map<String, ?> model) {
        this.viewName = viewName;
        this.model = model;
    }

    public ModelAndView(String viewName) {
        this.viewName = viewName;
    }

    public ModelAndView(Map<String, ?> model) {
        this.model = model;
    }

    public ModelAndView() {
    }

    public String getViewName() {
        return viewName;
    }

    public void setViewName(String viewName) {
        this.viewName = viewName;
    }

    public Map<String, ?> getModel() {
        return model;
    }

    public void setModel(Map<String, ?> model) {
        this.model = model;
    }
}
