package com.njuebay2.backend.service.impl;

import cn.dev33.satoken.secure.BCrypt;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.njuebay2.backend.domain.entity.User;
import com.njuebay2.backend.domain.vo.UserVO;
import com.njuebay2.backend.mapper.UserMapper;
import com.njuebay2.backend.service.UserService;
import com.njuebay2.backend.utils.RedisCache;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @author whm
 * @date 2023/12/4 15:37
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserMapper userMapper;

    private final RedisCache redisCache;

    @Override
    public Long login(String userName, String password) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUserName, userName);
        User user = userMapper.selectOne(queryWrapper);
        if (user == null || !BCrypt.checkpw(password, user.getPassword())) {
            return null;
        }
        user.setLastLoginTime(new Date());
        userMapper.updateById(user);
        return user.getUserId();
    }

    @Override
    public String register(UserVO userVO) {
        String verifyCode = redisCache.getCacheObject(userVO.getEmail());
        if (!verifyCode.equals(userVO.getVerifyCode())) {
            return "验证码错误";
        }
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUserName, userVO.getUserName());
        User user = userMapper.selectOne(queryWrapper);
        if (user != null) {
            return "用户名已存在";
        }

        queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getEmail, userVO.getEmail());
        user = userMapper.selectOne(queryWrapper);
        if (user != null) {
            return "该邮箱已经注册";
        }

        user = User.builder()
                .userName(userVO.getUserName())
                .password(BCrypt.hashpw(userVO.getPassword()))
                .email(userVO.getEmail())
                .createTime(new Date())
                .photo("https://kiyotakawang.oss-cn-hangzhou.aliyuncs.com/%E9%BB%98%E8%AE%A4%E5%A4%B4%E5%83%8F.jpg")
                .build();
        userMapper.insert(user);
        return "注册成功";
    }

    @Override
    public String update(UserVO userVO) {
        String verifyCode = redisCache.getCacheObject(userVO.getEmail());
        if (!verifyCode.equals(userVO.getVerifyCode())) {
            return "验证码错误";
        }
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getEmail, userVO.getEmail());
        User user = userMapper.selectOne(queryWrapper);
        if (user == null) {
            return "该邮箱未绑定用户";
        }
        user.setPassword(BCrypt.hashpw(userVO.getPassword()));
        userMapper.updateById(user);
        return "修改密码成功";
    }

    @Override
    public String editPhoto(Long userId, String url) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            return "用户不存在";
        }
        user.setPhoto(url);
        userMapper.updateById(user);
        return "修改用户头像成功";
    }

    @Override
    public User getUserInfo(String userName) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUserName, userName);
        User user = userMapper.selectOne(queryWrapper);
        if (user == null) {
            return null;
        }
        return User.builder()
                .userId(user.getUserId())
                .userName(user.getUserName())
                .email(user.getEmail())
                .createTime(user.getCreateTime())
                .photo(user.getPhoto())
                .build();
    }
}
