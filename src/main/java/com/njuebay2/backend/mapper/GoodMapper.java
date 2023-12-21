package com.njuebay2.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.njuebay2.backend.domain.entity.Good;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author whm
 * @date 2023/12/4 16:42
 */
@Mapper
public interface GoodMapper extends BaseMapper<Good> {
    @Select("select * from good where match(name, mainDesc) against(#{queryStr})")
    List<Good> queryFulltext(@Param("queryStr") String queryStr);
}
