package com.njuebay2.backend.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author whm
 * @date 2023/12/4 11:16
 */
@Data
@TableName("aliconfig")
public class AliConfig {
    @TableId("id")
    private Integer id;

    @TableField("accesskey")
    private String accessKey;

    @TableField("accesssecret")
    private String accessSecret;
}
