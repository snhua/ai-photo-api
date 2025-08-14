package com.aiphone.service.impl;

import com.aiphone.common.exception.CosResponse;
import com.aiphone.config.CosConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class CosService {

    @Autowired
    private CosConfig cosConfig;

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 生成COS文件路径
     */
    public String generateCosKey(String ext, String fileType, Long userId) {
        LocalDateTime now = LocalDateTime.now();
        String ymd = now.format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss_"));
        String random = String.format("%06d", new Random().nextInt(1000000));
        return String.format("images/%s/%d/%s/IMG_%s%s.%s", fileType,userId,
            now.format(DateTimeFormatter.ofPattern("yyyyMMdd")), ymd, random, ext);
    }

    /**
     * 生成临时密钥
     */
    public CosResponse generateTempKey(Long userId, String fileType, String ext) {
        // 验证参数
//        if (cosConfig.getSecretId() == null || cosConfig.getSecretKey() == null) {
//            return new CosResponse(-1, "secretId or secretKey not ready");
//        }
//        if (cosConfig.getBucket() == null || cosConfig.getRegion() == null) {
//            return new CosResponse(-1, "bucket or regions not ready");
//        }
//        if (!cosConfig.getExtWhiteList().contains(ext)) {
//            return new CosResponse(-1, "ext not allow");
//        }

        try {
            String cosHost = cosConfig.bucket + ".cos." + cosConfig.region + ".myqcloud.com";
            String cosKey = generateCosKey(ext,fileType,userId);
            
            long now = System.currentTimeMillis() / 1000;
            long exp = now + 900; // 15分钟过期
            String qKeyTime = now + ";" + exp;
            String qSignAlgorithm = "sha1";

            // 生成policy
            Map<String, Object> policy = new HashMap<>();
            policy.put("expiration", new java.util.Date(exp * 1000).toInstant().toString());
            
            Map<String, Object>[] conditions = new Map[5];
            conditions[0] = Mapof("q-sign-algorithm", qSignAlgorithm);
            conditions[1] = Mapof("q-ak", cosConfig.secretId);
            conditions[2] = Mapof("q-sign-time", qKeyTime);
            conditions[3] = Mapof("bucket", cosConfig.bucket);
            conditions[4] = Mapof("key", cosKey);
            
            policy.put("conditions", conditions);
            String policyJson = objectMapper.writeValueAsString(policy);

            // 步骤一：生成 SignKey
            String signKey = hmacSha1(cosConfig.secretKey, qKeyTime);

            // 步骤二：生成 StringToSign
            String stringToSign = sha1(policyJson);

            // 步骤三：生成 Signature
            String qSignature = hmacSha1(signKey, stringToSign);

            // 构建响应数据
            CosResponse.CosData data = new CosResponse.CosData();
            data.setCosHost(cosHost);
            data.setCosKey(cosKey);
            data.setPolicy(Base64.getEncoder().encodeToString(policyJson.getBytes(StandardCharsets.UTF_8)));
            data.setQSignAlgorithm(qSignAlgorithm);
            data.setQAk(cosConfig.secretId);
            data.setQKeyTime(qKeyTime);
            data.setQSignature(qSignature);

            return new CosResponse(0, "success", data);

        } catch (Exception e) {
            return new CosResponse(-1, "Error generating temp key: " + e.getMessage());
        }
    }

    private Map<String, Object> Mapof(String s, String qSignAlgorithm) {
        Map map = new HashMap();
        map.put(s,qSignAlgorithm);
        return map;
    }

    /**
     * HMAC-SHA1签名
     */
    private String hmacSha1(String key, String data) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA1");
        SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA1");
        mac.init(secretKeySpec);
        byte[] hash = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
        return bytesToHex(hash);
    }

    /**
     * SHA1哈希
     */
    private String sha1(String data) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-1");
        byte[] hash = digest.digest(data.getBytes(StandardCharsets.UTF_8));
        return bytesToHex(hash);
    }

    /**
     * 字节数组转十六进制字符串
     */
    private String bytesToHex(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        for (byte b : bytes) {
            result.append(String.format("%02x", b));
        }
        return result.toString();
    }
} 