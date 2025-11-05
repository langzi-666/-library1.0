package com.library.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.library.entity.Category;
import org.apache.ibatis.annotations.Mapper;

/**
 * 图书分类Mapper接口
 */
@Mapper
public interface CategoryMapper extends BaseMapper<Category> {
}

