package com.aiphone.service;

import java.util.Map;

/**
 * 系统配置服务接口
 */
public interface SystemConfigService {
    
    /**
     * 获取配置值
     */
    String getConfigValue(String key);
    
    /**
     * 获取配置值，带默认值
     */
    String getConfigValue(String key, String defaultValue);
    
    /**
     * 设置配置值
     */
    boolean setConfigValue(String key, String value);
    
    /**
     * 获取钱包相关配置
     */
    Map<String, Object> getWalletConfig();
}
