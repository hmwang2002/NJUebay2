package com.ebaynju.ebay_backend.service;

import com.ebaynju.ebay_backend.pojo.User;

import java.util.List;
import java.util.Map;

public interface UserService {
    List<User> allUser();
    // 注册
    Map<String, Object> register(String nickName, String password, String email, String checkPwd);
    // 邮箱验证
    boolean mailCheck(String jwt, String code);
    //登录
    Map<String, Object> login(String loginName, String password);
    // 根据id调取用户
    User queryUserById(int id);
    // 根据email寻找用户
    User queryUserByEmail(String email);
    // 根据用户名寻找用户
    User queryUserByNickName(String nickName);
    // 忘记密码token生成
    String forgetTokenGenerate(User user);
    //忘记密码 密码重置
    Map<String, Object> forgetChange(String token, String pwd, String checkPwd);
    Map<String, Object> addUser(User user);
    Map<String, Object> updateInfo(String token, String nickName);
    Map<String, Object> updatePwd(String token, String oldPwd, String newPwd, String checkPwd);
}
