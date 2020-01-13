package com.imooc.controller;

import com.imooc.pojo.Users;
import com.imooc.pojo.bo.UserBO;
import com.imooc.service.UserService;
import com.imooc.utils.CookieUtils;
import com.imooc.utils.IMOOCJSONResult;
import com.imooc.utils.JsonUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Api(value = "注册登录", tags = {"注册登录相关接口"})
@RestController
@RequestMapping("passport")
public class PassportController {

    @Autowired
    private UserService userService;

    @ApiOperation(value = "用户名是否存在", httpMethod = "GET")
    @GetMapping("/usernameIsExist")
    public IMOOCJSONResult hello(@RequestParam String username) {

        if (StringUtils.isBlank(username)) {
            return IMOOCJSONResult.errorMsg("用户名不能为空");
        }

        boolean isExist = userService.queryUsernameIsExist(username);
        if (isExist) {
            return IMOOCJSONResult.errorMsg("用户名已经存在");
        } else {
            return IMOOCJSONResult.ok();
        }
    }

    @ApiOperation(value = "注册用户", httpMethod = "POST")
    @PostMapping("/regist")
    public IMOOCJSONResult register(@RequestBody UserBO userBO, HttpServletRequest request, HttpServletResponse response) {

        String username = userBO.getUsername();
        String password = userBO.getPassword();
        String confirmPwd = userBO.getConfirmPassword();

        if (StringUtils.isBlank(username) || StringUtils.isBlank(password) || StringUtils.isBlank(confirmPwd)) {
            return IMOOCJSONResult.errorMsg("用户名或密码不能为空");
        }

        boolean isExist = userService.queryUsernameIsExist(username);
        if (isExist) {
            return IMOOCJSONResult.errorMsg("用户名已经存在");
        }

        if (password.length() < 6) {
            return IMOOCJSONResult.errorMsg("password length should longer than 6");
        }

        if (!password.equals(confirmPwd)) {
            return IMOOCJSONResult.errorMsg("pwd is not same as confirm pwd");
        }

        Users user = userService.createUser(userBO);

        user = setNullProperty(user);

        CookieUtils.setCookie(request, response, "user",
                JsonUtils.objectToJson(user), true);

        return IMOOCJSONResult.ok(user);
    }

    @ApiOperation(value = "用户登录", httpMethod = "POST")
    @PostMapping("/login")
    public IMOOCJSONResult login(@RequestBody UserBO userBO, HttpServletRequest request,
                                 HttpServletResponse response) {

        String username = userBO.getUsername();
        String password = userBO.getPassword();

        if (StringUtils.isBlank(username) || StringUtils.isBlank(password)) {
            return IMOOCJSONResult.errorMsg("用户名或密码不能为空");
        }

        Users user = userService.queryUserForLogin(userBO.getUsername(), userBO.getPassword());

        if (user == null) {
            return IMOOCJSONResult.errorMsg("用户名密码不匹配");
        }

        user = setNullProperty(user);

        CookieUtils.setCookie(request, response, "user", JsonUtils.objectToJson(user), true);

        return IMOOCJSONResult.ok(user);
    }

    private Users setNullProperty(Users userResult) {
        userResult.setPassword(null);
        userResult.setMobile(null);
        userResult.setEmail(null);
        userResult.setCreatedTime(null);
        userResult.setUpdatedTime(null);
        userResult.setBirthday(null);
        return userResult;
    }
}
