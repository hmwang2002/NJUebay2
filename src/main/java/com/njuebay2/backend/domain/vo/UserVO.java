package com.njuebay2.backend.domain.vo;

import lombok.Data;

/**
 * @author whm
 * @date 2023/12/4 15:39
 */
@Data
public class UserVO {
    private String userName;

    private String password;

    private String email;

    private String verifyCode;
}
