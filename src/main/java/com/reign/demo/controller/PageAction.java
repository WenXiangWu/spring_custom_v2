package com.reign.demo.controller;

import com.reign.demo.service.IQueryService;
import com.reign.spring.framework.annotation.Autowired;
import com.reign.spring.framework.annotation.Controller;
import com.reign.spring.framework.annotation.RequestMapping;
import com.reign.spring.framework.annotation.RequestParam;
import com.reign.spring.framework.webmvc.servlet.ModelAndView;
import java.util.HashMap;
import java.util.Map;

/**
 * 公布接口url
 * @author Tom
 *
 */
@Controller
@RequestMapping("/")
public class PageAction {

    @Autowired
    IQueryService queryService;

    @RequestMapping("/first.html")
    public ModelAndView query(@RequestParam("giao") String giao){
        String result = queryService.query(giao);
        Map<String,Object> model = new HashMap<String,Object>();
        model.put("giao", giao);
        model.put("data", result);
        model.put("token", "123456");
        return new ModelAndView("first.html",model);
    }

}
