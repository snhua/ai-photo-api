package com.aiphone.service.impl;

import com.aiphone.common.exception.CosResponse;
import com.aiphone.service.CosService;
import com.aiphone.util.FileUtil;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.model.GeneratePresignedUrlRequest;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.model.PutObjectResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tencent.cloud.*;
import com.tencent.cloud.cos.util.Jackson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClients;

/**
 * 腾讯云COS服务实现类
 */
@Slf4j
@Service
public class CosServiceImpl implements CosService {

    @Autowired
    private COSClient cosClient;

    @Value("${tencent.cos.bucket-name}")
    private String bucketName;

    @Value("${tencent.cos.region}")
    private String region;

    @Value("${tencent.cos.secret-id}")
    private String secretId;

    @Value("${tencent.cos.secret-key}")
    private String secretKey;

    @Value("${tencent.cos.role-arn:}")
    private String roleArn;

    @Value("${tencent.cos.role-session-name:cos-upload-session}")
    private String roleSessionName;

    @Value("${tencent.cos.enable-sts:true}")
    private boolean enableSts;

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final HttpClient httpClient = HttpClients.createDefault();
    @Autowired
    com.aiphone.service.impl.CosService service;

    @Override
    public String uploadFileToCos(MultipartFile file, String fileName) {
        try {
            // 创建临时文件
            File tempFile = File.createTempFile("cos_upload_", "_" + file.getOriginalFilename());
            file.transferTo(tempFile);
            
            // 上传到COS
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, fileName, tempFile);
            PutObjectResult putObjectResult = cosClient.putObject(putObjectRequest);
            
            // 删除临时文件
            tempFile.delete();
            
            // 生成文件URL
            return generateFileUrl(fileName);
        } catch (IOException e) {
            log.error("上传文件到COS失败", e);
            throw new RuntimeException("上传文件失败", e);
        }
    }

    @Override
    public CosResponse getUploadPolicy(Long userId, String fileName, String fileType) {

       return service.generateTempKey(userId,fileType,FileUtil.getFileExtension(fileName));
    }
    public Map<String, Object> _getUploadPolicy(Long userId, String fileName, String fileType) {
        try {
            // 调用腾讯云STS服务获取临时密钥
            Map<String, String> stsCredentials = getStsCredentials();
            // 生成对象键
            String objectKey = generateObjectKey(userId, fileName, fileType);
            
            // 设置过期时间（1小时）
            Date expiration = new Date(System.currentTimeMillis() + 3600 * 1000);
            String keyTime = generateKeyTime();

            // 构建临时密钥策略 - 使用正确的格式
            Map<String, Object> policy = new HashMap<>();
            policy.put("expiration", expiration.toInstant().toString());
            policy.put("conditions", new Object[]{
                new HashMap<String, Object>() {{
                    put("bucket", bucketName);
                }},
                new HashMap<String, Object>() {{
                    put("key", objectKey);
                }},
//                new HashMap<String, Object>() {{
//                    put("content-length-range", new Object[]{0, 10485760}); // 10MB限制
//                }},

                      new HashMap<String, Object>() {{
                    put("q-sign-algorithm", "sha1"); // 10MB限制
                }},  new HashMap<String, Object>() {{
                    put("q-ak", stsCredentials.get("tmpSecretId")); // 10MB限制
                }},
 new HashMap<String, Object>() {{
                    put("q-sign-time", keyTime); // 10MB限制
                }},

            });

            // 将policy转换为JSON字符串并进行Base64编码
            String policyJson = convertPolicyToJson(policy);
            String policyBase64 = Base64.getEncoder().encodeToString(policyJson.getBytes("UTF-8"));
            

            
            // 构建返回结果
            Map<String, Object> result = new HashMap<>();
            result.put("sessionToken", stsCredentials.get("sessionToken"));
            result.put("policy", policyBase64);
//            result.put("policy", policyJson);
            result.put("qSignAlgorithm", "sha1");
            result.put("qAk", stsCredentials.get("tmpSecretId"));
//            result.put("qSignTime", keyTime);
            result.put("qKeyTime", keyTime);
            result.put("qSignKey", generateSignKey(stsCredentials.get("tmpSecretKey")));
            result.put("qSignature", generateSignature(policyBase64, stsCredentials.get("tmpSecretKey")));
            result.put("objectKey", objectKey);
            result.put("bucket", bucketName);
            result.put("region", region);
            result.put("expire", expiration.getTime() / 1000);
            
            return result;
        } catch (Exception e) {
            log.error("生成临时密钥失败", e);
            throw new RuntimeException("生成临时密钥失败: " + e.getMessage());
        }
    }

    @Override
    public String generateFileUrl(String objectKey) {
        try {
            // 生成文件URL（有效期1年）
            Date expiration = new Date(System.currentTimeMillis() + 365 * 24 * 3600 * 1000L);
            GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(bucketName, objectKey);
            request.setExpiration(expiration);
            
            URL url = cosClient.generatePresignedUrl(request);
            return url.toString();
        } catch (Exception e) {
            log.error("生成文件URL失败", e);
            throw new RuntimeException("生成文件URL失败: " + e.getMessage());
        }
    }

    @Override
    public boolean deleteFile(String objectKey) {
        try {
            cosClient.deleteObject(bucketName, objectKey);
            return true;
        } catch (Exception e) {
            log.error("删除文件失败", e);
            return false;
        }
    }

    /**
     * 调用腾讯云STS云API获取临时密钥
     */
    private Map<String, String> getStsCredentials(){
        TreeMap<String, Object> config = new TreeMap<String, Object>();
        try {
            //这里的 SecretId 和 SecretKey 代表了用于申请临时密钥的永久身份（主账号、子账号等），子账号需要具有操作存储桶的权限。
            // 替换为您的云 api 密钥 SecretId
            config.put("secretId", secretId);
            // 替换为您的云 api 密钥 SecretKey
            config.put("secretKey", secretKey);

            // 初始化 policy
            Policy policy = new Policy();

            // 设置域名:
            // 如果您使用了腾讯云 cvm，可以设置内部域名
            //config.put("host", "sts.internal.tencentcloudapi.com");

            // 临时密钥有效时长，单位是秒，默认 1800 秒，目前主账号最长 2 小时（即 7200 秒），子账号最长 36 小时（即 129600）秒
            config.put("durationSeconds", 1800);
            // 换成您的 bucket
            config.put("bucket", "aiphoto-1304396619");
            // 换成 bucket 所在地区
            config.put("region", "ap-guangzhou");

            // 开始构建一条 statement
            Statement statement = new Statement();
            // 声明设置的结果是允许操作
            statement.setEffect("allow");
            /**
             * 密钥的权限列表。必须在这里指定本次临时密钥所需要的权限。
             * 权限列表请参见 https://cloud.tencent.com/document/product/436/31923
             * 规则为 {project}:{interfaceName}
             * project : 产品缩写  cos相关授权为值为cos,数据万象(数据处理)相关授权值为ci
             * 授权所有接口用*表示，例如 cos:*,ci:*
             * 添加一批操作权限 :
             */
            statement.addActions(new String[]{
                    "cos:PutObject",
                    // 表单上传、小程序上传
//                    "cos:PostObject",
//                    // 分块上传
//                    "cos:InitiateMultipartUpload",
//                    "cos:ListMultipartUploads",
//                    "cos:ListParts",
//                    "cos:UploadPart",
//                    "cos:CompleteMultipartUpload",
                    // 处理相关接口一般为数据万象产品 权限中以ci开头
                    // 创建媒体处理任务
//                    "ci:CreateMediaJobs",
                    // 文件压缩
//                    "ci:CreateFileProcessJobs"
            });

            /**
             * 这里改成允许的路径前缀，可以根据自己网站的用户登录态判断允许上传的具体路径
             * 资源表达式规则分对象存储(cos)和数据万象(ci)两种
             * 数据处理、审核相关接口需要授予ci资源权限
             *  cos : qcs::cos:{region}:uid/{appid}:{bucket}/{path}
             *  ci  : qcs::ci:{region}:uid/{appid}:bucket/{bucket}/{path}
             * 列举几种典型的{path}授权场景：
             * 1、允许访问所有对象："*"
             * 2、允许访问指定的对象："a/a1.txt", "b/b1.txt"
             * 3、允许访问指定前缀的对象："a*", "a/*", "b/*"
             *  如果填写了“*”，将允许用户访问所有资源；除非业务需要，否则请按照最小权限原则授予用户相应的访问权限范围。
             *
             * 示例：授权examplebucket-1250000000 bucket目录下的所有资源给cos和ci 授权两条Resource
             */
            statement.addResources(new String[]{
                    "*"
//                    "qcs::cos:ap-guangzhou:uid/100016876368:aiphoto-1304396619/*"

//                    "qcs::cos:ap-chongqing:uid/1250000000:examplebucket-1250000000/*",
//                    "qcs::ci:ap-chongqing:uid/1250000000:bucket/examplebucket-1250000000/*"
            });

            // 把一条 statement 添加到 policy
            // 可以添加多条
            policy.addStatement(statement);
            // 将 Policy 示例转化成 String，可以使用任何 json 转化方式，这里是本 SDK 自带的推荐方式
            config.put("policy", Jackson.toJsonPrettyString(policy));

            Response response = CosStsClient.getCredential(config);
            System.out.println(response.credentials.tmpSecretId);
            System.out.println(response.credentials.tmpSecretKey);
            System.out.println(response.credentials.sessionToken);
            {
                // 解析响应
                Credentials credentials = response.credentials;

                // 构建返回结果
                Map<String, String> result = new HashMap<>();
                result.put("tmpSecretId", credentials.tmpSecretId);
                result.put("tmpSecretKey", credentials.tmpSecretKey);
                result.put("sessionToken",credentials.sessionToken);

                log.info("STS云API调用成功，获取到临时密钥");
                return result;
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalArgumentException("no valid secret !");
        }
    }

    /**
     * 获取模拟的临时密钥（用于开发测试）
     * 这个方案可以确保在任何情况下都能正常工作
     */
    private Map<String, String> getMockCredentials() {
        Map<String, String> result = new HashMap<>();
        result.put("tmpSecretId", secretId);
        result.put("tmpSecretKey", secretKey);
        result.put("sessionToken", "mock-session-token-" + System.currentTimeMillis());
        
        log.info("使用模拟临时密钥，适用于开发测试环境");
        return result;
    }

    /**
     * 生成对象键
     */
    private String generateObjectKey(Long userId, String fileName, String fileType) {
        String timestamp = String.valueOf(System.currentTimeMillis());
        String extension = getFileExtension(fileName);
        return String.format("uploads/%s/%d/%s.%s", fileType, userId, timestamp, extension);
    }

    /**
     * 获取文件扩展名
     */
    private String getFileExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            return "jpg";
        }
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }

    /**
     * 生成密钥时间
     */
    private String generateKeyTime() {
        long startTime = System.currentTimeMillis() / 1000;
        long endTime = startTime + 3600; // 1小时
        return startTime + ";" + endTime;
    }

    /**
     * 生成签名密钥
     */
    private String generateSignKey(String tmpSecretKey) {
        try {
            // 使用HMAC-SHA1算法生成签名密钥
            javax.crypto.Mac mac = javax.crypto.Mac.getInstance("HmacSHA1");
            javax.crypto.spec.SecretKeySpec secretKeySpec = new javax.crypto.spec.SecretKeySpec(
                tmpSecretKey.getBytes("UTF-8"), "HmacSHA1");
            mac.init(secretKeySpec);
            
            String keyTime = generateKeyTime();
            byte[] signKey = mac.doFinal(keyTime.getBytes("UTF-8"));
            return Base64.getEncoder().encodeToString(signKey);
        } catch (Exception e) {
            log.error("生成签名密钥失败", e);
            return "sign-key-" + tmpSecretKey.hashCode();
        }
    }

    /**
     * 生成签名
     */
    private String generateSignature(String policyBase64, String tmpSecretKey) {
        try {
            // 使用HMAC-SHA1算法对policy进行签名
            javax.crypto.Mac mac = javax.crypto.Mac.getInstance("HmacSHA1");
            javax.crypto.spec.SecretKeySpec secretKeySpec = new javax.crypto.spec.SecretKeySpec(
                tmpSecretKey.getBytes("UTF-8"), "HmacSHA1");
            mac.init(secretKeySpec);
            
            byte[] signature = mac.doFinal(policyBase64.getBytes("UTF-8"));
            return Base64.getEncoder().encodeToString(signature);
        } catch (Exception e) {
            log.error("生成签名失败", e);
            return "signature-" + policyBase64.hashCode();
        }
    }

    /**
     * 将policy转换为JSON字符串
     */
    private String convertPolicyToJson(Map<String, Object> policy) {
        // 简单的JSON转换，实际项目中可以使用Jackson或Gson
        StringBuilder json = new StringBuilder();
        json.append("{");
        json.append("\"expiration\":\"").append(policy.get("expiration")).append("\",");
        json.append("\"conditions\":[");
        
        Object[] conditions = (Object[]) policy.get("conditions");
        for (int i = 0; i < conditions.length; i++) {
            Map<String, Object> condition = (Map<String, Object>) conditions[i];
            json.append("{");
            boolean first = true;
            for (Map.Entry<String, Object> entry : condition.entrySet()) {
                if (!first) {
                    json.append(",");
                }
                json.append("\"").append(entry.getKey()).append("\":");
                if (entry.getValue() instanceof String) {
                    json.append("\"").append(entry.getValue()).append("\"");
                } else if (entry.getValue() instanceof Object[]) {
                    json.append("[");
                    Object[] array = (Object[]) entry.getValue();
                    for (int j = 0; j < array.length; j++) {
                        if (j > 0) json.append(",");
                        json.append(array[j]);
                    }
                    json.append("]");
                } else {
                    json.append(entry.getValue());
                }
                first = false;
            }
            json.append("}");
            if (i < conditions.length - 1) {
                json.append(",");
            }
        }
        json.append("]}");
        
        return json.toString();
    }
} 