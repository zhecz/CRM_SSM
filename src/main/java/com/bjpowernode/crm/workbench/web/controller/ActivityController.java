package com.bjpowernode.crm.workbench.web.controller;


import com.bjpowernode.crm.settings.domain.User;
import com.bjpowernode.crm.settings.service.UserService;
import com.bjpowernode.crm.settings.service.impl.UserServiceImpl;
import com.bjpowernode.crm.utils.DateTimeUtil;
import com.bjpowernode.crm.utils.PrintJson;
import com.bjpowernode.crm.utils.ServiceFactory;
import com.bjpowernode.crm.utils.UUIDUtil;
import com.bjpowernode.crm.vo.PaginationVO;
import com.bjpowernode.crm.workbench.domain.Activity;
import com.bjpowernode.crm.workbench.domain.ActivityRemark;
import com.bjpowernode.crm.workbench.service.ActivityService;

import com.bjpowernode.crm.workbench.service.impl.ActivityServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.util.*;

@Controller
public class ActivityController {

    @Autowired
    private UserService userService;

    @Autowired
    private ActivityService activityService;




    @RequestMapping("/workbench/activity/getUserList.do")
    public @ResponseBody List<User> getUserList(){

        System.out.println("取得用户信息列表");

        List<User> uList = userService.getUserList();

        return uList;

    }

    @RequestMapping("/workbench/activity/save.do")
    public @ResponseBody Map<String, Boolean> saveCreateActivity(HttpServletRequest request, HttpServletResponse response){

        System.out.println("执行市场活动添加操作");

        String id = UUIDUtil.getUUID();
        String owner = request.getParameter("owner");
        String name = request.getParameter("name");
        String startDate = request.getParameter("startDate");
        String endDate = request.getParameter("endDate");
        String cost = request.getParameter("cost");
        String description = request.getParameter("description");
        //创建时间：当前系统时间
        String createTime = DateTimeUtil.getSysTime();
        //创建人：当前登录用户
        String createBy = ((User)request.getSession().getAttribute("user")).getName();

        System.out.println("Created" + createBy);

        Activity a = new Activity();
        a.setId(id);
        a.setCost(cost);
        a.setStartDate(startDate);
        a.setOwner(owner);
        a.setName(name);
        a.setEndDate(endDate);
        a.setDescription(description);
        a.setCreateTime(createTime);
        a.setCreateBy(createBy);

        boolean flag = activityService.save(a);
        Map<String, Boolean> map = new HashMap<>();
        map.put("success",flag);

        return map;

    }




    @RequestMapping("/workbench/activity/pageList.do")
    @ResponseBody
    private PaginationVO<Activity> pageList(HttpServletRequest request, HttpServletResponse response) {

        System.out.println("进入到查询市场活动信息列表的操作（结合条件查询+分页查询）");

        String name = request.getParameter("name");
        String owner = request.getParameter("owner");
        String startDate = request.getParameter("startDate");
        String endDate = request.getParameter("endDate");
        String pageNoStr = request.getParameter("pageNo");
        int pageNo = Integer.valueOf(pageNoStr);
        //每页展现的记录数
        String pageSizeStr = request.getParameter("pageSize");
        int pageSize = Integer.valueOf(pageSizeStr);
        //计算出略过的记录数
        int skipCount = (pageNo-1)*pageSize;

        Map<String,Object> map = new HashMap<String,Object>();
        map.put("name", name);
        map.put("owner", owner);
        map.put("startDate",startDate);
        map.put("endDate",endDate);
        map.put("skipCount",skipCount);
        map.put("pageSize",pageSize);


        PaginationVO<Activity> vo = activityService.pageList(map);

        return vo;

        //vo--> {"total":100,"dataList":[{市场活动1},{2},{3}]}

    }





    @RequestMapping("/workbench/activity/delete.do")
    @ResponseBody
    private Map<String,Boolean> delete(HttpServletRequest request, HttpServletResponse response) {

        System.out.println("执行市场活动的删除操作");

        String ids[] = request.getParameterValues("id");

        boolean flag = activityService.delete(ids);

        Map<String, Boolean> map = new HashMap<>();
        map.put("success", flag);
        return map;



    }

    @RequestMapping("/workbench/activity/getUserListAndActivity.do")
    @ResponseBody
    private Map<String,Object> getUserListAndActivity(HttpServletRequest request, HttpServletResponse response) {

        System.out.println("进入到查询用户信息列表和根据市场活动id查询单条记录的操作");

        String id = request.getParameter("id");

        Map<String,Object> map = activityService.getUserListAndActivity(id);

        return map;


    }

    @RequestMapping("/workbench/activity/update.do")
    @ResponseBody
    private Map<String, Boolean> update(HttpServletRequest request, HttpServletResponse response) {

        System.out.println("执行市场活动修改操作");

        String id = request.getParameter("id");
        String owner = request.getParameter("owner");
        String name = request.getParameter("name");
        String startDate = request.getParameter("startDate");
        String endDate = request.getParameter("endDate");
        String cost = request.getParameter("cost");
        String description = request.getParameter("description");
        //修改时间：当前系统时间
        String editTime = DateTimeUtil.getSysTime();
        //修改人：当前登录用户
        String editBy = ((User)request.getSession().getAttribute("user")).getName();

        Activity a = new Activity();
        a.setId(id);
        a.setCost(cost);
        a.setStartDate(startDate);
        a.setOwner(owner);
        a.setName(name);
        a.setEndDate(endDate);
        a.setDescription(description);
        a.setEditBy(editBy);
        a.setEditTime(editTime);


        boolean flag = activityService.update(a);

        Map<String,Boolean> map = new HashMap<>();
        map.put("success", flag);

        return map;
    }


    @RequestMapping("/workbench/activity/detail.do")
    private String detail(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException {

        System.out.println("进入到跳转到详细信息页的操作");

        String id = request.getParameter("id");


        Activity a = activityService.detail(id);

        request.setAttribute("a", a);

        return "/workbench/activity/detail";



    }


    @RequestMapping("/workbench/activity/getRemarkListByAid.do")
    @ResponseBody
    private List<ActivityRemark> getRemarkListByAid(HttpServletRequest request, HttpServletResponse response) {

        System.out.println("根据市场活动id，取得备注信息列表");

        String activityId = request.getParameter("activityId");

        List<ActivityRemark> arList = activityService.getRemarkListByAid(activityId);

        return arList;
    }


    @RequestMapping("/workbench/activity/deleteRemark.do")
    @ResponseBody
    private Map<String,Boolean> deleteRemark(HttpServletRequest request, HttpServletResponse response) {

        System.out.println("删除备注操作");

        String id = request.getParameter("id");

        boolean flag = activityService.deleteRemark(id);

        Map<String,Boolean> map = new HashMap<>();
        map.put("success", flag);

        return map;


    }


    @RequestMapping("/workbench/activity/saveRemark.do")
    @ResponseBody
    private Map<String,Object> saveRemark(HttpServletRequest request, HttpServletResponse response) {

        System.out.println("执行添加备注操作");

        String noteContent = request.getParameter("noteContent");
        String activityId = request.getParameter("activityId");
        String id = UUIDUtil.getUUID();
        String createTime = DateTimeUtil.getSysTime();
        String createBy = ((User)request.getSession().getAttribute("user")).getName();
        String editFlag = "0";

        ActivityRemark ar = new ActivityRemark();
        ar.setId(id);
        ar.setNoteContent(noteContent);
        ar.setActivityId(activityId);
        ar.setCreateBy(createBy);
        ar.setCreateTime(createTime);
        ar.setEditFlag(editFlag);

        boolean flag = activityService.saveRemark(ar);

        Map<String,Object> map = new HashMap<String,Object>();
        map.put("success", flag);
        map.put("ar", ar);

        return map;

    }






    @RequestMapping("/workbench/activity/updateRemark.do")
    @ResponseBody
    private Map<String, Object> updateRemark(HttpServletRequest request, HttpServletResponse response) {

        System.out.println("执行修改备注的操作");

        String id = request.getParameter("id");
        String noteContent = request.getParameter("noteContent");
        String editTime = DateTimeUtil.getSysTime();
        String editBy = ((User)request.getSession().getAttribute("user")).getName();
        String editFlag = "1";

        ActivityRemark ar = new ActivityRemark();

        ar.setId(id);
        ar.setNoteContent(noteContent);
        ar.setEditFlag(editFlag);
        ar.setEditBy(editBy);
        ar.setEditTime(editTime);


        boolean flag = activityService.updateRemark(ar);

        Map<String,Object> map = new HashMap<String,Object>();
        map.put("success", flag);
        map.put("ar", ar);

        return map;

    }








}
