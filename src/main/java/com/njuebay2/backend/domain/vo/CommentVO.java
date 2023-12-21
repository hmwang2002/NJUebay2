package com.njuebay2.backend.domain.vo;

import lombok.Data;

@Data
public class CommentVO {
    private Long commentId;

    private String userName;

    private String content;

    private String createTime;

    private String avatar;
}
