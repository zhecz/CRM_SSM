package com.bjpowernode.crm.settings.web.controller;


import com.bjpowernode.crm.settings.domain.User;
import com.bjpowernode.crm.settings.service.UserService;
import com.bjpowernode.crm.utils.MD5Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

@Controller
public class UserController {

    @Autowired
    private UserService userService;


    @RequestMapping("/settings/user/login.do")
    public @ResponseBody Map<String, Object> login(HttpServletRequest request, HttpServletResponse response){

        System.out.println("进入到验证登录操作");

        String loginAct = request.getParameter("loginAct");
        String loginPwd = request.getParameter("loginPwd");
        //将密码的明文形式转换为MD5的密文形式
        loginPwd = MD5Util.getMD5(loginPwd);
        //接收浏览器端的ip地址
        String ip = request.getRemoteAddr();
        System.out.println("--------------ip:"+ip);



        try {

            User user = userService.login(loginAct,loginPwd,ip);

            request.getSession().setAttribute("user", user);

            Map<String, Object> map = new HashMap<String, Object>();
            map.put("success", true);
            map.put("msg", null);

            return map;

        }catch(Exception e){
            e.printStackTrace();


            String msg = e.getMessage();

            Map<String,Object> map = new HashMap<String,Object>();
            map.put("success", false);
            map.put("msg", msg);

            return map;

        }

    }
}
