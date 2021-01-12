package com.reign.demo.service.impl;


import com.reign.spring.framework.annotation.Service;
import com.reign.demo.service.UserService;

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
    public String getUserName(String name) {
        return name+"hahhahh";
    }
}
