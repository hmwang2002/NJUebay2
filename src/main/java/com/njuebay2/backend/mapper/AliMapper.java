package com.njuebay2.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.njuebay2.backend.domain.entity.AliConfig;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author whm
 * @date 2023/12/4 15:40
 */
@Mapper
public interface AliMapper extends BaseMapper<AliConfig> {
}
