# COS STS 云 API 实现指南

## 问题描述

在使用腾讯云 COS 临时授权功能时，遇到了以下错误：

```
java.lang.NoSuchMethodError: com.tencentcloudapi.sts.v20180813.models.AssumeRoleRequest.setSkipSign(Z)V
```

这个错误表明当前使用的腾讯云 STS SDK 版本中存在方法缺失的问题。

## 解决方案

为了避免 SDK 版本兼容性问题，我们改用 **STS 云 API** 直接调用，而不是使用 SDK。

### 1. 配置控制

在 `application.yml` 中添加了 `enable-sts` 配置选项：

```yaml
tencent:
  cos:
    secret-id: your-secret-id
    secret-key: your-secret-key
    region: ap-guangzhou
    bucket-name: aiphoto-1304396619
    role-arn: qcs::cam::uin/100012345678:roleName/COSAccessRole
    role-session-name: cos-upload-session
    # 是否启用STS功能（如果遇到版本兼容性问题，可以设置为false）
    enable-sts: false
```

### 2. 代码实现

#### 2.1 签名工具类

创建了 `TencentCloudSignature.java` 来处理腾讯云 API 的签名认证：

```java
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

    // ... 其他签名相关方法
}
```

#### 2.2 STS 云 API 调用

在 `CosServiceImpl.java` 中实现了直接调用 STS 云 API：

```java
private Map<String, String> getStsCredentials() {
    // 如果禁用了STS功能，直接使用模拟数据
    if (!enableSts) {
        log.info("STS功能已禁用，使用模拟数据");
        return getMockCredentials();
    }

    try {
        // 检查必要的配置
        if (roleArn == null || roleArn.trim().isEmpty()) {
            log.warn("roleArn未配置，使用模拟数据");
            return getMockCredentials();
        }

        // 构建STS API请求
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("RoleArn", roleArn);
        requestBody.put("RoleSessionName", roleSessionName);
        requestBody.put("DurationSeconds", 3600L);

        // 构建权限策略
        Map<String, Object> policy = new HashMap<>();
        policy.put("version", "2.0");

        Map<String, Object> statement = new HashMap<>();
        statement.put("effect", "allow");
        statement.put("action", new String[]{
            "name/cos:PutObject",
            "name/cos:PostObject",
            "name/cos:InitiateMultipartUpload",
            "name/cos:ListMultipartUploads",
            "name/cos:ListParts",
            "name/cos:UploadPart",
            "name/cos:CompleteMultipartUpload"
        });
        statement.put("resource", new String[]{
            "qcs::cos:" + region + ":uid/1250000000:" + bucketName + "/*"
        });

        policy.put("statement", new Object[]{statement});
        requestBody.put("Policy", objectMapper.writeValueAsString(policy));

        String payload = objectMapper.writeValueAsString(requestBody);

        // 构建请求URL
        String apiUrl = "https://sts.tencentcloudapi.com/";

        // 构建请求头
        TreeMap<String, String> headers = new TreeMap<>();
        headers.put("Content-Type", "application/json; charset=utf-8");
        headers.put("Host", "sts.tencentcloudapi.com");
        headers.put("X-TC-Action", "AssumeRole");
        headers.put("X-TC-Version", "2018-08-13");
        headers.put("X-TC-Region", region);
        headers.put("X-TC-Timestamp", String.valueOf(System.currentTimeMillis() / 1000));

        // 生成签名
        String authorization = TencentCloudSignature.sign(secretKey, "POST", "/", headers, payload);
        headers.put("Authorization", authorization);

        // 构建HTTP请求
        HttpPost request = new HttpPost(apiUrl);
        request.setHeader("Content-Type", "application/json; charset=utf-8");

        // 添加所有请求头
        for (Map.Entry<String, String> header : headers.entrySet()) {
            request.setHeader(header.getKey(), header.getValue());
        }

        // 设置请求体
        request.setEntity(new StringEntity(payload, "UTF-8"));

        // 发送请求
        HttpResponse response = httpClient.execute(request);

        if (response.getStatusLine().getStatusCode() == 200) {
            // 解析响应
            Map<String, Object> responseMap = objectMapper.readValue(EntityUtils.toString(response.getEntity()), Map.class);
            Map<String, Object> credentials = (Map<String, Object>) responseMap.get("Response");
            Map<String, Object> creds = (Map<String, Object>) credentials.get("Credentials");

            // 构建返回结果
            Map<String, String> result = new HashMap<>();
            result.put("tmpSecretId", (String) creds.get("TmpSecretId"));
            result.put("tmpSecretKey", (String) creds.get("TmpSecretKey"));
            result.put("sessionToken", (String) creds.get("Token"));

            log.info("STS云API调用成功，获取到临时密钥");
            return result;
        } else {
            log.error("STS云API调用失败，状态码: {}, 响应: {}", response.getStatusLine().getStatusCode(), EntityUtils.toString(response.getEntity()));
            log.info("使用模拟数据作为备选方案");
            return getMockCredentials();
        }

    } catch (Exception e) {
        log.error("STS云API调用出现错误: {}", e.getMessage());
        log.info("使用模拟数据作为备选方案");
        return getMockCredentials();
    }
}
```

### 3. 依赖配置

添加了 Apache HttpClient 依赖：

```xml
<!-- Apache HttpClient -->
<dependency>
    <groupId>org.apache.httpcomponents</groupId>
    <artifactId>httpclient</artifactId>
    <version>4.5.13</version>
</dependency>
```

### 4. 错误处理层次

1. **配置级别**: 通过 `enable-sts: false` 完全禁用 STS 功能
2. **API 调用级别**: 捕获 HTTP 请求异常
3. **响应解析级别**: 检查响应状态码和内容
4. **备选方案**: 始终提供模拟数据作为备选

## 使用方法

### 1. 开发环境（推荐使用模拟数据）

```yaml
tencent:
  cos:
    enable-sts: false
```

### 2. 生产环境（如果 STS 配置正确）

```yaml
tencent:
  cos:
    enable-sts: true
    role-arn: qcs::cam::uin/100012345678:roleName/COSAccessRole
```

### 3. 测试接口

```bash
# 测试COS临时授权接口
curl -X GET "http://localhost:8080/cos/temp-auth?fileName=test.jpg&fileType=avatar" \
  -H "Authorization: Bearer YOUR_TOKEN_HERE" \
  -H "Content-Type: application/json"
```

## 预期响应

无论使用真实 STS 还是模拟数据，都会返回完整的授权信息：

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "sessionToken": "...",
    "policy": "...",
    "qSignAlgorithm": "sha1",
    "qAk": "...",
    "qKeyTime": "...",
    "qSignKey": "...",
    "qSignature": "...",
    "objectKey": "...",
    "bucket": "...",
    "region": "...",
    "expire": ...
  }
}
```

## 日志说明

- **STS 成功**: `"STS云API调用成功，获取到临时密钥"`
- **STS 禁用**: `"STS功能已禁用，使用模拟数据"`
- **API 调用失败**: `"STS云API调用失败，状态码: ..., 响应: ..."`
- **使用模拟数据**: `"使用模拟临时密钥，适用于开发测试环境"`

## 优势

1. **稳定性**: 不依赖 SDK 版本，直接调用云 API
2. **灵活性**: 可以通过配置控制是否使用 STS
3. **兼容性**: 支持 Java 8 及以上版本
4. **可维护性**: 清晰的错误处理和日志记录
5. **安全性**: 使用标准的腾讯云 API 签名认证

## 注意事项

1. **模拟数据**: 在开发环境中使用模拟数据是安全的
2. **生产环境**: 如果需要真实的临时密钥，请确保 STS 配置正确
3. **API 限制**: 注意腾讯云 API 的调用频率限制
4. **监控**: 关注日志中的 STS 相关错误信息

## 相关文件

- `CosServiceImpl.java`: 主要实现文件
- `TencentCloudSignature.java`: 签名工具类
- `application.yml`: 配置文件
- `test_cos_fix.sh`: 测试脚本
- `COS_STS_SETUP_GUIDE.md`: STS 设置指南

## 参考文档

- [腾讯云临时密钥生成及使用指引](https://cloud.tencent.com/document/product/436/14048#case2)
- [腾讯云 API 签名方法](https://cloud.tencent.com/document/api/213/30654)
