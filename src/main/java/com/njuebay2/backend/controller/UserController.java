package com.njuebay2.backend.controller;

import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import com.njuebay2.backend.domain.vo.Response;
import com.njuebay2.backend.domain.vo.UserVO;
import com.njuebay2.backend.service.MailService;
import com.njuebay2.backend.service.OssService;
import com.njuebay2.backend.service.UserService;
import jakarta.validation.constraints.Email;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Random;

/**
 * @author whm
 * @date 2023/12/4 15:54
 */
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    private final MailService mailService;

    private final OssService ossService;

    private final Random random = new Random();

    /**
     * 发送邮箱验证码
     * @param email
     * @return
     */
    @RequestMapping("/sendCode")
    public Response<?> sendCode(@RequestParam("email") @Email String email) {
        int code = random.nextInt(100000, 999999);
        mailService.sendSimpleMail(email, "NJUebay注册验证码", String.valueOf(code));
        return Response.success(200, "发送验证码成功");
    }

    /**
     * 用户登录
     * @param userName
     * @param password
     * @return
     */
    @RequestMapping("/login")
    public Response<?> login(@RequestParam("userName") String userName, @RequestParam("password") String password) {
        Long id = userService.login(userName, password);
        if (id != null) {
            StpUtil.login(id);
            SaTokenInfo saTokenInfo = StpUtil.getTokenInfo();
            return Response.success(200, "登录成功！", saTokenInfo);
        } else {
            return Response.failed(999, "用户名或密码错误！");
        }
    }

    /**
     * 注册账号
     * @param userVO
     * @return
     */
    @RequestMapping("/register")
    public Response<?> register(@RequestBody UserVO userVO) {
        String res = userService.register(userVO);
        if (res.equals("注册成功")) {
            return Response.success(200, "注册成功！");
        } else {
            return Response.failed(999, res);
        }
    }

    /**
     * 退出登录
     * @return
     */
    @RequestMapping("/logout")
    public Response<?> logout() {
        StpUtil.logout();
        return Response.success(200, "退出成功！");
    }

    /**
     * 修改密码
     * @param userVO
     * @return
     */
    @PostMapping("/update")
    public Response<?> update(@RequestBody UserVO userVO) {
        String res = userService.update(userVO);
        if (res.equals("修改密码成功")) {
            return Response.success(200, "修改密码成功！");
        } else {
            return Response.failed(999, res);
        }
    }

    /**
     * 更新用户头像
     */
    @PostMapping("/editPhoto")
    public Response<String> editPhoto(@RequestParam("userName") String userName, @RequestParam("newPhoto") MultipartFile file) {
        String url = ossService.uploadFile(file);
        if (url == null) {
            return Response.failed(999, "图片上传失败");
        }
        String res = userService.editPhoto(userName, url);
        if (res.equals("用户不存在")) {
            return Response.failed(999, res);
        } else {
            return Response.success(200, res, url);
        }
    }
}
