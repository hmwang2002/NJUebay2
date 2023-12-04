package com.njuebay2.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.njuebay2.backend.domain.entity.Good;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author whm
 * @date 2023/12/4 16:42
 */
@Mapper
public interface GoodMapper extends BaseMapper<Good> {
}
