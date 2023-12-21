package com.njuebay2.backend.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.NonNull;

import java.util.Date;

@Data
@TableName("comment")
public class Comment {
    @TableId(value = "comment_id", type = IdType.AUTO)
    private Long commentId;

    @TableField("user_id")
    @NonNull
    private Long userId;

    @TableField("good_id")
    @NonNull
    private Long goodId;

    @TableField("content")
    private String content;

    @TableField("create_time")
    private String createTime;

    public Comment(@NonNull Long userId, @NonNull Long goodId, String content, String createTime) {
        this.userId = userId;
        this.goodId = goodId;
        this.content = content;
        this.createTime = createTime;
    }
}
