package com.reign.demo.service.impl;


import com.reign.spring.framework.annotation.Service;
import com.reign.demo.service.UserService;
import com.reign.spring.framework.webmvc.servlet.ModelAndView;

import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName UserServiceImpl
 * @Description TODO
 * @Author wuwenxiang
 * @Date 2021-01-11 16:24
 * @Version 1.0
 **/
@Service
public class UserServiceImpl implements UserService {
    @Override
    public ModelAndView getUserName(String name) {
        ModelAndView modelAndView = new ModelAndView();
        Map<String,String> map = new HashMap<String, String>();
        map.put("name",name+"hahhahh");
        modelAndView.setModel(map);
        return modelAndView;
    }
}
