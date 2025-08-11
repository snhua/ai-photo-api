package com.aiphone.util;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.TreeMap;

/**
 * 腾讯云API签名工具类
 */
public class TencentCloudSignature {

    /**
     * 生成腾讯云API签名
     */
    public static String sign(String secretKey, String httpMethod, String uri, TreeMap<String, String> headers, String payload) throws Exception {
        // 1. 生成规范请求串
        String canonicalRequest = buildCanonicalRequest(httpMethod, uri, headers, payload);
        
        // 2. 生成待签名字符串
        String stringToSign = buildStringToSign(headers, canonicalRequest);
        
        // 3. 计算签名
        return calculateSignature(secretKey, stringToSign);
    }

    /**
     * 构建规范请求串
     */
    private static String buildCanonicalRequest(String httpMethod, String uri, TreeMap<String, String> headers, String payload) throws Exception {
        StringBuilder canonicalRequest = new StringBuilder();
        
        // HTTPRequestMethod
        canonicalRequest.append(httpMethod).append("\n");
        
        // CanonicalURI
        canonicalRequest.append(uri).append("\n");
        
        // CanonicalQueryString (空)
        canonicalRequest.append("\n");
        
        // CanonicalHeaders
        for (String key : headers.keySet()) {
            canonicalRequest.append(key.toLowerCase()).append(":").append(headers.get(key)).append("\n");
        }
        canonicalRequest.append("\n");
        
        // SignedHeaders
        StringBuilder signedHeaders = new StringBuilder();
        for (String key : headers.keySet()) {
            if (signedHeaders.length() > 0) {
                signedHeaders.append(";");
            }
            signedHeaders.append(key.toLowerCase());
        }
        canonicalRequest.append(signedHeaders.toString()).append("\n");
        
        // HashedRequestPayload
        String hashedPayload = sha256Hex(payload);
        canonicalRequest.append(hashedPayload);
        
        return canonicalRequest.toString();
    }

    /**
     * 构建待签名字符串
     */
    private static String buildStringToSign(TreeMap<String, String> headers, String canonicalRequest) throws Exception {
        StringBuilder stringToSign = new StringBuilder();
        
        // Algorithm
        stringToSign.append("TC3-HMAC-SHA256").append("\n");
        
        // RequestTimestamp
        String timestamp = headers.get("X-TC-Timestamp");
        stringToSign.append(timestamp).append("\n");
        
        // CredentialScope
        String date = timestamp.substring(0, 8);
        String service = "sts";
        String credentialScope = date + "/" + service + "/tc3_request";
        stringToSign.append(credentialScope).append("\n");
        
        // HashedCanonicalRequest
        String hashedCanonicalRequest = sha256Hex(canonicalRequest);
        stringToSign.append(hashedCanonicalRequest);
        
        return stringToSign.toString();
    }

    /**
     * 计算签名
     */
    private static String calculateSignature(String secretKey, String stringToSign) throws Exception {
        // 获取当前日期
        String date = String.valueOf(System.currentTimeMillis() / 1000).substring(0, 8);
        
        // 计算 secretDate
        byte[] secretDate = hmacSha256(("TC3" + secretKey).getBytes(StandardCharsets.UTF_8), date);
        
        // 计算 secretService
        byte[] secretService = hmacSha256(secretDate, "sts");
        
        // 计算 secretSigning
        byte[] secretSigning = hmacSha256(secretService, "tc3_request");
        
        // 计算签名
        byte[] signature = hmacSha256(secretSigning, stringToSign);
        
        return Base64.getEncoder().encodeToString(signature);
    }

    /**
     * HMAC-SHA256 签名
     */
    private static byte[] hmacSha256(byte[] key, String data) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKeySpec = new SecretKeySpec(key, "HmacSHA256");
        mac.init(secretKeySpec);
        return mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * SHA256 哈希
     */
    private static String sha256Hex(String data) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(data.getBytes(StandardCharsets.UTF_8));
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }
} 