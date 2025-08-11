package com.aiphone.mapper;

import com.aiphone.entity.SystemConfig;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 系统配置Mapper接口
 */
@Mapper
public interface SystemConfigMapper extends BaseMapper<SystemConfig> {
    
    /**
     * 根据配置键获取配置值
     */
    @Select("SELECT config_value FROM system_configs WHERE config_key = #{configKey}")
    String getConfigValue(@Param("configKey") String configKey);
    
    /**
     * 根据配置键获取配置对象
     */
    @Select("SELECT * FROM system_configs WHERE config_key = #{configKey}")
    SystemConfig getByConfigKey(@Param("configKey") String configKey);
}