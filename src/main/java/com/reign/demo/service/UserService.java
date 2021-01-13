package com.reign.demo.service;

import com.reign.spring.framework.webmvc.servlet.ModelAndView;

/**
 * @ClassName UserService
 * @Description TODO
 * @Author wuwenxiang
 * @Date 2021-01-11 16:24
 * @Version 1.0
 **/
public interface UserService {
    ModelAndView getUserName(String name);
}
