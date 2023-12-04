package com.njuebay2.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.njuebay2.backend.domain.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author whm
 * @date 2023/12/4 15:41
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

}
