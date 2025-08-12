package com.aiphone.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 微信支付配置
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "wechat.pay")
public class WechatPayConfig {
    private String apiV3Key;

    /**
     * 应用ID
     */
    private String appId;

    /**
     * 商户号
     */
    private String mchId;

    /**
     * 商户API密钥
     */
    private String mchKey;

    /**
     * 商户API证书路径
     */
    private String certPath;

    /**
     * 商户API证书密码
     */
    private String certPassword;

    /**
     * 支付结果通知URL
     */
    private String notifyUrl;

    /**
     * 退款结果通知URL
     */
    private String refundNotifyUrl;

    /**
     * 是否沙箱环境
     */
    private boolean sandbox = false;

    /**
     * 沙箱环境密钥
     */
    private String sandboxKey;

    /**
     * 支付超时时间（分钟）
     */
    private int timeoutMinutes = 30;
} 