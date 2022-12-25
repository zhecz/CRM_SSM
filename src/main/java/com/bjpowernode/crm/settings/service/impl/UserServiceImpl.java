package com.bjpowernode.crm.settings.service.impl;

import com.bjpowernode.crm.exception.LoginException;
import com.bjpowernode.crm.settings.dao.UserDao;
import com.bjpowernode.crm.settings.domain.User;
import com.bjpowernode.crm.settings.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("userService")
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDao userDao;



    public User login(String loginAct, String loginPwd, String ip) throws LoginException {

        Map<String,String> map = new HashMap<String,String>();
        map.put("loginAct", loginAct);
        map.put("loginPwd", loginPwd);

        User user = userDao.login(map);

        if(user==null){

            throw new LoginException("账号密码错误");

        }


        //判断锁定状态
        String lockState = user.getLockState();
        if("0".equals(lockState)){

            throw new LoginException("账号已锁定");

        }


        return user;
    }

    public List<User> getUserList() {

        List<User> uList = userDao.getUserList();

        return uList;
    }
}




































