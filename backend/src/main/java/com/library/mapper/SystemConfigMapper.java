package com.library.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.library.entity.SystemConfig;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 系统设置Mapper接口
 */
@Mapper
public interface SystemConfigMapper extends BaseMapper<SystemConfig> {
    
    /**
     * 根据配置键查询配置值
     * @param configKey 配置键
     * @return 配置值
     */
    String getConfigValue(@Param("configKey") String configKey);
    
    /**
     * 更新配置值
     * @param configKey 配置键
     * @param configValue 配置值
     * @return 更新行数
     */
    int updateConfigValue(@Param("configKey") String configKey, @Param("configValue") String configValue);
}

