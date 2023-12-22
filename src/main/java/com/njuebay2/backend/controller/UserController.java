package com.njuebay2.backend.controller;

import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import com.njuebay2.backend.domain.entity.User;
import com.njuebay2.backend.domain.vo.Response;
import com.njuebay2.backend.domain.vo.UserVO;
import com.njuebay2.backend.service.MailService;
import com.njuebay2.backend.service.OssService;
import com.njuebay2.backend.service.UserService;
import jakarta.validation.constraints.Email;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * @author whm
 * @date 2023/12/4 15:54
 */
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@CrossOrigin
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

    @PostMapping("/sendCode")
    public Response<?> sendCode(@RequestParam("email") @Email String email) {
        try {
            int code = random.nextInt(100000, 999999);
            boolean isMailSent = mailService.sendSimpleMail(email, "NJUebay注册验证码", String.valueOf(code));

            if (isMailSent) {
                // 如果邮件发送成功
                return Response.success(200, "验证码已发送成功");
            } else {
                // 如果邮件发送失败
                return Response.failed(500, "验证码发送失败");
            }
        } catch (Exception e) {
            // 如果遇到其他异常
            return Response.failed(503, "发送验证码时发生异常");
        }
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
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("userId", id);
            responseData.put("saTokenInfo", saTokenInfo);
            return Response.success(200, "登录成功！", responseData);
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
    public Response<String> editPhoto(@RequestParam("newPhoto") MultipartFile file) {
        String url = ossService.uploadFile(file);
        if (url == null) {
            return Response.failed(999, "图片上传失败");
        }
        Long userId = StpUtil.getLoginIdAsLong();
        String res = userService.editPhoto(userId, url);
        if (res.equals("用户不存在")) {
            return Response.failed(999, res);
        } else {
            return Response.success(200, res, url);
        }
    }

    @PostMapping("/getUser")
    public Response<User> getUser(@RequestParam("userName") String userName) {
        // 获得用户自己的信息
        User user = userService.getUserInfo(userName);
        return user == null ? Response.failed(999, "用户不存在") : Response.success(200, "获取用户信息成功", user);
    }

    @RequestMapping("eval")
    public Response<?> eval(@RequestParam("goodId") Long goodId, @RequestParam("userName") String userName, @RequestParam("score") Integer score) {
        if (StpUtil.isLogin()) {
            String res = userService.eval(goodId, userName, score);
            if (res.equals("评价成功")) {
                return Response.success(200, res);
            } else {
                return Response.failed(999, res);
            }
        } else {
            return Response.failed(999, "用户未登录");
        }
    }
}
