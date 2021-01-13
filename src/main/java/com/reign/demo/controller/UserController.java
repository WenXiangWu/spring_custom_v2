package com.reign.demo.controller;



import com.reign.demo.service.UserService;
import com.reign.spring.framework.annotation.Autowired;
import com.reign.spring.framework.annotation.Controller;
import com.reign.spring.framework.annotation.RequestMapping;
import com.reign.spring.framework.annotation.RequestParam;
import com.reign.spring.framework.webmvc.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @ClassName UserController
 * @Description TODO
 * @Author wuwenxiang
 * @Date 2021-01-11 16:23
 * @Version 1.0
 **/
@Controller
@RequestMapping("/userController")
public class UserController {

    @Autowired
    private UserService userService;

    @RequestMapping("/getUserName*")
    public ModelAndView getUserName(HttpServletRequest req, HttpServletResponse resp, @RequestParam("userName") String userName){
        return userService.getUserName(userName);
    }

}
