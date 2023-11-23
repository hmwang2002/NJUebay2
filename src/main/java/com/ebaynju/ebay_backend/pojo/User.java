package com.ebaynju.ebay_backend.pojo;

import lombok.Data;

/**
 * @author cardigan
 * @version 1.0
 * Create by 2022/11/26
 */

@Data
public class User {
    private int userId; // 学号
    private String nickName; //不超过15字符
    private String password; //不超过15字符
    private String email;
    private String salt;
}
