package com.ebaynju.ebay_backend.controller;

import com.ebaynju.ebay_backend.pojo.User;
import com.ebaynju.ebay_backend.service.Impl.JwtServiceImpl;
import com.ebaynju.ebay_backend.service.Impl.MailServiceImpl;
import com.ebaynju.ebay_backend.service.Impl.UserServiceImpl;
import com.ebaynju.ebay_backend.util.JsonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author cardigan
 * @version 1.0
 * Create by 2022/11/26
 */
@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private MailServiceImpl mailService;

    @Autowired
    private JwtServiceImpl jwtService;


    /**
     * 检查用户输入验证码正确性
     * @param token 携带用户注册信息的token
     * @param code
     * @return
     * @throws JSONException
     */
    @RequestMapping(value = "/mailCheck", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    @CrossOrigin
    public String MailCheck(@RequestParam("token") String token, @RequestParam("code") String code) throws JSONException{
        Map<String, Object> map = new HashMap<>();

        if (!userService.mailCheck(token, code)) {
            map.put("msg", "验证码不正确！");
            return JsonUtil.getJSONString(1, map);
        }

        return JsonUtil.getJSONString(0);
    }

    /**
     * 发送邮件
     * @param type 发送邮件类型 (注册/忘记密码)
     * @param token
     * @return
     * @throws JSONException
     */
    @RequestMapping(value = "/sendEmail", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    @CrossOrigin
    public String SendEmail(@RequestParam("type") String type, @RequestParam("token") String token) throws JSONException {
        Map<String, Object> map = new HashMap<>();

        String nickName = (String) jwtService.getInfo(token, "nickName").get("nickName");
        String email = (String) jwtService.getInfo(token, "email").get("email");
        String code = (String) jwtService.getInfo(token, "code").get("code");

        mailService.sendSimpleMail(email, nickName + " " + type, code);

        return JsonUtil.getJSONString(0, map);
    }

    /**
     * 用户注册 检查合法性
     * @param nickName
     * @param password
     * @param email
     * @return 包括携带用户信息的加密token
     * @throws JSONException
     */
    @RequestMapping(value = "/register", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    @CrossOrigin
    public String Register(@RequestParam("nickName") String nickName, @RequestParam("password") String password,
                           @RequestParam("email") String email, @RequestParam("checkPwd") String checkPwd)
            throws JSONException {
        Map<String, Object> us = userService.register(nickName, password, email, checkPwd);

        if (us.containsKey("msg")) {
            return JsonUtil.getJSONString(1, us);
        }

/*        // 发送验证邮件, 可改为html
        mailService.sendSimpleMail(email, nickName + "Check", (String) us.get("code"));*/

        return JsonUtil.getJSONString(0, us);
    }

    @RequestMapping(value = "/addUser", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    @CrossOrigin
    public String AddUser(@RequestParam("token") String token) throws JSONException {
        String nickName = (String) jwtService.getInfo(token, "nickName").get("nickName");
        String password = (String) jwtService.getInfo(token, "password").get("password");
        String email = (String) jwtService.getInfo(token, "email").get("email");
        String salt = (String) jwtService.getInfo(token, "salt").get("salt");

        User user = new User();
        user.setEmail(email);
        user.setPassword(password);
        user.setNickName(nickName);
        user.setSalt(salt);

        Map<String, Object> map = userService.addUser(user);

        return JsonUtil.getJSONString(0, map);
    }

    /**
     * 用户登录
     * @param loginName
     * @param password
     * @return
     * @throws JSONException
     */
    @RequestMapping(value = "/login", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    @CrossOrigin
    public String Login(@RequestParam("loginName") String loginName, @RequestParam("password") String password)
            throws JSONException {
        Map<String, Object> us = userService.login(loginName, password);

        if(us.containsKey("msg")) {
            return JsonUtil.getJSONString(1, us);
        }

        return JsonUtil.getJSONString(0, us);
    }

    /**
     * 忘记密码: 检查输入邮箱是否存在并发送验证码
     * @param email
     * @return
     * @throws JSONException
     */
    @RequestMapping(value = "/forget", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    @CrossOrigin
    public String Forget(@RequestParam("email") String email) throws JSONException {
        Map<String, Object> map = new HashMap<>();

        if (userService.queryUserByEmail(email) == null) {
            map.put("msg", "未找到对应用户");
            return JsonUtil.getJSONString(1, map);
        }

        User user = userService.queryUserByEmail(email);
        String token = userService.forgetTokenGenerate(user);

/*        // 发送验证邮件, 可改为html
        mailService.sendSimpleMail(email, "Check",
                (String) jwtService.getInfo(token, "nickName").get("nickName")
                        + jwtService.getInfo(token, "code").get("code"));*/

        map.put("token", token);

        return JsonUtil.getJSONString(0, map);
    }

    /**
     * 重置密码
     * @param token
     * @param pwd
     * @param checkPwd
     * @return
     * @throws JSONException
     */
    @RequestMapping(value = "/forget/change", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    @CrossOrigin
    public String ForgetChange(@RequestParam("token") String token, @RequestParam("pwd") String pwd,
                               @RequestParam("checkPwd") String checkPwd) throws JSONException {
        Map<String, Object> map = userService.forgetChange(token, pwd, checkPwd);

        if (map.containsKey("msg")) {
            return JsonUtil.getJSONString(1, map);
        }

        return JsonUtil.getJSONString(0);
    }

    @RequestMapping(value = "/updateInfo", method = {RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    @CrossOrigin
    public String updateInfo(@RequestParam("token") String token, @RequestParam("nickName") String nickName)
            throws JSONException {
        Map<String, Object> map = new HashMap<>();

        map = userService.updateInfo(token, nickName);

        if (map.containsKey("msg")) {
            return JsonUtil.getJSONString(1, map);
        }

        return JsonUtil.getJSONString(0, map);
    }

    /**
     * 更新用户密码
     * @param token
     * @param oldPwd
     * @param newPwd
     * @param checkPwd
     * @return
     * @throws JSONException
     */
    @RequestMapping(value = "/updatePwd", method = {RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    @CrossOrigin
    public String updatePwd(@RequestParam("token") String token, @RequestParam("oldPwd") String oldPwd,
                            @RequestParam("newPwd") String newPwd, @RequestParam("checkPwd") String checkPwd) throws JSONException {
        Map<String, Object> map = new HashMap<>();

        map = userService.updatePwd(token, oldPwd, newPwd, checkPwd);

        if (map.containsKey("msg")) {
            return JsonUtil.getJSONString(1, map);
        }

        return JsonUtil.getJSONString(0, map);
    }
}
