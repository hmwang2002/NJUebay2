package com.njuebay2.backend.service.impl;

import cn.dev33.satoken.secure.BCrypt;
import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.njuebay2.backend.domain.entity.Good;
import com.njuebay2.backend.domain.entity.User;
import com.njuebay2.backend.domain.vo.UserEditInfoVO;
import com.njuebay2.backend.domain.vo.UserVO;
import com.njuebay2.backend.mapper.GoodMapper;
import com.njuebay2.backend.mapper.UserMapper;
import com.njuebay2.backend.service.UserService;
import com.njuebay2.backend.utils.RedisCache;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Objects;

/**
 * @author whm
 * @date 2023/12/4 15:37
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserMapper userMapper;

    private final RedisCache redisCache;

    private final GoodMapper goodMapper;

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
                .address(userVO.getAddress())
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
        return user;
    }

    @Override
    public User getCurrentUserInfo() {
        Long userId = StpUtil.getLoginIdAsLong();
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUserId, userId);
        User user = userMapper.selectOne(queryWrapper);
        if (user == null) {
            return null;
        }
        return user;
    }

    @Override
    public String eval(Long goodId, String userName, Integer score) {
        Good good = goodMapper.selectById(goodId);
        if (good == null) return "商品不存在";
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUserName, userName);
        User user = userMapper.selectOne(queryWrapper);
        if (user == null) return "用户不存在";
        user.setEvalNum(user.getEvalNum() + 1);
        user.setAvgScore((user.getAvgScore() * (user.getEvalNum() - 1) + score) / user.getEvalNum());
        userMapper.updateById(user);

        Long userId = user.getUserId();
        if (Objects.equals(good.getSellerId(), userId)) {
            good.setBuyerEval(true);
        } else if (Objects.equals(good.getBuyerId(), userId)){
            good.setSellerEval(true);
        } else {
            return "评价失败，该用户既不是卖家也不是买家";
        }
        goodMapper.updateById(good);
        return "评价成功";
    }

    @Override
    public String edit(Long userId, UserEditInfoVO userEditInfoVO) {
        User user = userMapper.selectById(userId);
        if (user == null) return "用户不存在";
        user.setUserName(userEditInfoVO.getUserName());
        user.setAddress(userEditInfoVO.getAddress());
        user.setPhoto(userEditInfoVO.getPhoto());
        user.setEmail(userEditInfoVO.getEmail());
        userMapper.updateById(user);
        return "修改用户信息成功";
    }
}
