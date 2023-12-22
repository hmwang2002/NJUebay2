package com.njuebay2.backend.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.validation.constraints.Email;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

/**
 * @author whm
 * @date 2023/12/4 11:11
 */
@Data
@Builder
@TableName("user")
public class User {
    @TableId(value = "user_id", type = IdType.AUTO)
    private Long userId;

    @TableField("user_name")
    private String userName;

    @TableField("password")
    private String password;

    @TableField("email")
    @Email
    private String email;

    @TableField("create_time")
    private Date createTime;

    @TableField("last_login_time")
    private Date lastLoginTime;

    @TableField("photo")
    private String photo;

    @TableField("avg_score")
    private Double avgScore;

    @TableField("eval_num")
    private Integer evalNum;
}
