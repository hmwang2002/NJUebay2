package com.ebaynju.ebay_backend.mapper;

import com.ebaynju.ebay_backend.pojo.User;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

@Mapper
@Component
public interface UserMapper {
    void addUser(User user);
    int deleteUser(int id);
    User queryUserById(int id);
    User queryUserByNickName(String nickName);
    User queryUserByEmail(String email);
    int updateUser(User user);
    void changePwd(String nickName, String password, String salt);
}
