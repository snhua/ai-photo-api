package com.aiphone.service.impl;

import com.aiphone.service.SystemConfigService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 系统配置服务实现类
 */
@Service
public class SystemConfigServiceImpl implements SystemConfigService {
    
    @Override
    public String getConfigValue(String key) {
        // 这里可以从数据库或配置文件读取
        // 暂时返回默认值
        return getDefaultConfigValue(key);
    }
    
    @Override
    public String getConfigValue(String key, String defaultValue) {
        String value = getConfigValue(key);
        return value != null ? value : defaultValue;
    }
    
    @Override
    public boolean setConfigValue(String key, String value) {
        // 这里可以保存到数据库或配置文件
        // 暂时返回true
        return true;
    }
    
    @Override
    public Map<String, Object> getWalletConfig() {
        Map<String, Object> config = new HashMap<>();
        config.put("serviceFeeRate", "0.2");
        config.put("withdrawalDelayDays", "7");
        config.put("minWithdrawalAmount", "10.00");
        config.put("maxWithdrawalAmount", "50000.00");
        config.put("dailyWithdrawalLimit", "100000.00");
        config.put("withdrawalFeeRate", "0.02");
        config.put("minWithdrawalFee", "1.00");
        config.put("maxWithdrawalFee", "50.00");
        return config;
    }
    
    private String getDefaultConfigValue(String key) {
        switch (key) {
            case "serviceFeeRate":
                return "0.2";
            case "withdrawalDelayDays":
                return "7";
            case "minWithdrawalAmount":
                return "10.00";
            case "maxWithdrawalAmount":
                return "50000.00";
            case "dailyWithdrawalLimit":
                return "100000.00";
            case "withdrawalFeeRate":
                return "0.02";
            case "minWithdrawalFee":
                return "1.00";
            case "maxWithdrawalFee":
                return "50.00";
            default:
                return null;
        }
    }
}
