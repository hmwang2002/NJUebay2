package com.njuebay2.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.njuebay2.backend.domain.entity.Comment;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CommentMapper extends BaseMapper<Comment> {
}
