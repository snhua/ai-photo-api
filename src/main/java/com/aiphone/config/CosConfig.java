package com.aiphone.config;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.region.Region;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 腾讯云COS配置类
 */
@Configuration
public class CosConfig {

    @Value("${tencent.cos.secret-id}")
    public String secretId;
    @Value("${tencent.cos.bucket-name}")
    public String bucket;

    @Value("${tencent.cos.secret-key}")
    public String secretKey;

    @Value("${tencent.cos.region}")
    public String region;

    @Bean
    public COSClient cosClient() {
        COSCredentials credentials = new BasicCOSCredentials(secretId, secretKey);
        ClientConfig clientConfig = new ClientConfig(new Region(region));
        return new COSClient(credentials, clientConfig);
    }
} 