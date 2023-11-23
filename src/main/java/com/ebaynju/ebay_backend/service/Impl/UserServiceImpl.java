package com.ebaynju.ebay_backend.service.Impl;

import com.ebaynju.ebay_backend.mapper.UserMapper;
import com.ebaynju.ebay_backend.pojo.User;
import com.ebaynju.ebay_backend.util.CodeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.apache.commons.codec.digest.DigestUtils;

import java.util.*;
import java.util.regex.Pattern;

/**
 * @author cardigan
 * @version 1.0
 * Create by 2022/11/26
 */
@Service
public class UserServiceImpl {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    JwtServiceImpl jwtService;

    /**
     * 电子邮箱正则表达式
     */
    String REGEX_EMAIL = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
    /**
     * 密码正则表达式: 8-20位数字、字母、特殊符号(区分大小写), 必须包含两种以上
     */
    String REGEX_PWD = "^(?![0-9]+$)(?![a-z]+$)(?![A-Z]+$)(?!([^(0-9a-zA-Z)])+$).{8,20}$";
    /**
     * 昵称正则表达式: 3-12位汉字、英文字母或数字
     */
    String REGEX_NICKNAME = "[A-Za-z0-9_\\-\\u4e00-\\u9fa5]{3,12}";

    public User queryUserById(int id) {
        return userMapper.queryUserById(id);
    }
    public User queryUserByEmail(String email) {
        return userMapper.queryUserByEmail(email);
    }
    public User queryUserByNickName(String nickName) {
        return userMapper.queryUserByNickName(nickName);
    }
    public Map<String, Object> register(String nickName, String password, String email, String checkPwd) {
        Map<String, Object> map = new HashMap<>();

        User user4Check = userMapper.queryUserByNickName(nickName);
        // 用户名已被注册
        if (user4Check != null) {
            map.put("msg", "此用户名已被注册");
            return map;
        }

        // 邮箱已被注册
        user4Check = userMapper.queryUserByEmail(email);
        if (user4Check != null) {
            map.put("msg", "此邮箱已被注册！");
            return map;
        }

        // 用户名为空
        if (nickName.isEmpty()) {
            map.put("msg", "用户名不能为空");
            return map;
        }

        // 密码为空
        if (password.isEmpty()) {
            map.put("msg", "密码不能为空");
            return map;
        }

        // 用户名格式错误
        if (!Pattern.matches(REGEX_NICKNAME, nickName)) {
            map.put("msg", "用户名格式错误，应为3-12位汉字、英文字母或数字");
            return map;
        }

        // 密码格式错误
        if (!Pattern.matches(REGEX_PWD, password)) {
            map.put("msg", "密码格式错误，应为8-20位数字、字母、特殊符号(区分大小写), 且必须包含两种以上");
            return map;
        }

        if (!password.equals(checkPwd)) {
            map.put("msg", "两次输入密码不一致！");
            return map;
        }

        User user = new User();
        user.setNickName(nickName);
        // 设置盐值, 为加密做准备
        user.setSalt(UUID.randomUUID().toString().substring(0, 5));
        user.setPassword(DigestUtils.md5Hex(password + user.getSalt()));
        user.setEmail(email);

        String code = CodeUtil.generateCode(6);

        map.put("code", code);
        map.put("email", user.getEmail());
        map.put("nickName", user.getNickName());
        map.put("userId", user.getUserId());
        map.put("password", user.getPassword());
        map.put("salt", user.getSalt());

        // 生成身份token
        String token = jwtService.generateToken(map);
        map.put("token", token);

        return map;
    }

    // 邮箱验证
    public boolean mailCheck(String jwt, String code) {
        // 从jwt token获取信息
        String email = (String) jwtService.getInfo(jwt, "email").get("email");
        String nickName = (String) jwtService.getInfo(jwt, "nickName").get("nickName");
        String code4Check = (String) jwtService.getInfo(jwt, "code").get("code");

        return code.equals(code4Check);
    }

    public Map<String, Object> login(String loginName, String password) {
        Map<String, Object> map = new HashMap<>();
        boolean emailCheck = false;

        // 用户名为空
        if (loginName.isEmpty()) {
            map.put("msg", "用户名不能为空");
            return map;
        }

        // 密码为空
        if (password.isEmpty()) {
            map.put("msg", "密码不能为空");
            return map;
        }

        User user = new User();

        // 判断为邮箱还是为昵称
        if (Pattern.matches(REGEX_EMAIL, loginName)) {
            emailCheck = true;
        }

        // 获取用户
        if (emailCheck) {
            user = userMapper.queryUserByEmail(loginName);
        } else {
            user= userMapper.queryUserByNickName(loginName);
        }

        // 用户不存在
        if (user == null) {
            map.put("msg", "用户不存在");
            return map;
        }

        if (!DigestUtils.md5Hex(password + user.getSalt()).equals(user.getPassword())) {
            map.put("msg", "密码不正确");
            return map;
        }

        map.put("email", user.getEmail());
        map.put("userId", user.getUserId());
        map.put("nickName", user.getNickName());
        map.put("salt", user.getSalt());
        String token = jwtService.generateToken(map);
        map.put("token", token);

        return map;
    }

    public String forgetTokenGenerate(User user) {
        Map<String, Object> map = new HashMap<>();

        String nickName = user.getNickName();
        String email = user.getEmail();
        int userId = user.getUserId();

        map.put("email", email);
        map.put("nickName", nickName);
        map.put("userId", userId);
        String code = CodeUtil.generateCode(6);
        map.put("code", code);
        String token = jwtService.generateToken(map);

        return token;
    }

    public Map<String, Object> forgetChange(String token, String pwd, String checkPwd) {
        Map<String, Object> map = new HashMap<>();

        String nickName = (String) jwtService.getInfo(token, "nickName").get("nickName");

        // 密码格式错误
        if (!Pattern.matches(REGEX_PWD, pwd)) {
            map.put("msg", "密码格式错误，应为8-20位数字、字母、特殊符号(区分大小写), 且必须包含两种以上");
            return map;
        }

        if (!pwd.equals(checkPwd)) {
            map.put("msg", "两次输入密码不一致！");
            return map;
        }

        String salt = UUID.randomUUID().toString().substring(0, 5);
        pwd = DigestUtils.md5Hex(checkPwd + salt);

        userMapper.changePwd(nickName, pwd, salt);

        return map;
    }

    public Map<String, Object> addUser(User user) {
        userMapper.addUser(user);

        Map<String, Object> map = new HashMap<>();

        String nickName = user.getNickName();
        String password = user.getPassword();
        String email = user.getEmail();

        map.put("nickName", nickName);
        map.put("password", password);
        map.put("email", email);

        return map;
    }

    public Map<String, Object> updateInfo(String token, String nickName) {
        Map<String, Object> map = new HashMap<>();

        int id = (Integer) jwtService.getInfo(token, "userId").get("userId");
        User user = userMapper.queryUserById(id);

        if (nickName.equals(user.getNickName())) {
            map.put("msg", "更改用户名与原来一致！");
            return map;
        }

        User user4check = userMapper.queryUserByNickName(nickName);

        if (user4check != null) {
            map.put("msg", "用户名已存在！");
            return map;
        }

        user.setNickName(nickName);
        userMapper.updateUser(user);

        map.put("id", id);
        map.put("nickName", nickName);

        return map;
    }

    public Map<String, Object> updatePwd(String token,String oldPwd, String newPwd, String checkPwd) {
        Map<String, Object> map = new HashMap<>();

        int id = (Integer) jwtService.getInfo(token, "userId").get("userId");
        User user = userMapper.queryUserById(id);

        if (!DigestUtils.md5Hex(oldPwd + user.getSalt()).equals(user.getPassword())) {
            map.put("msg", "旧密码不正确");
            return map;
        }

        // 密码格式错误
        if (!Pattern.matches(REGEX_PWD, newPwd)) {
            map.put("msg", "密码格式错误，应为8-20位数字、字母、特殊符号(区分大小写), 且必须包含两种以上");
            return map;
        }

        if (!newPwd.equals(checkPwd)) {
            map.put("msg", "两次输入密码不一致！");
            return map;
        }

        user.setPassword(DigestUtils.md5Hex(newPwd + user.getSalt()));
        userMapper.updateUser(user);

        map.put("id", id);
        map.put("nickName", user.getNickName());

        return map;
    }
}
