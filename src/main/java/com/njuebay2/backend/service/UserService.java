package com.njuebay2.backend.service;

import com.njuebay2.backend.domain.entity.User;
import com.njuebay2.backend.domain.vo.UserEditInfoVO;
import com.njuebay2.backend.domain.vo.UserVO;

/**
 * @author whm
 * @date 2023/12/4 15:36
 */
public interface UserService {
    Long login(String userName, String password);

    String register(UserVO userVO);

    String update(UserVO userVO);

    String editPhoto(Long userId, String url);

    User getUserInfo(String userName);

    User getCurrentUserInfo();

    String eval(Long goodId, String userName, Integer score);

    String edit(Long userId, UserEditInfoVO userEditInfoVO);
}
