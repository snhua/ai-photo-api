# 腾讯云 COS 临时授权接口说明

## 接口概述

`/api/cos/temp-auth` 接口用于获取腾讯云 COS 的临时上传授权信息，返回包含 `sessionToken`、`policy`、`qSignAlgorithm` 等参数的完整临时密钥信息。

## 接口详情

### 请求信息

- **接口地址**: `GET /api/cos/temp-auth`
- **请求方式**: GET
- **认证要求**: 需要 JWT token

### 请求参数

| 参数名   | 类型   | 必填 | 默认值 | 说明                                    |
| -------- | ------ | ---- | ------ | --------------------------------------- |
| fileName | String | 是   | -      | 文件名                                  |
| fileType | String | 否   | avatar | 文件类型（avatar/artwork/reference 等） |

### 响应格式

#### 成功响应

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "sessionToken": "session-token-1640995200000",
    "policy": "eyJleHBpcmF0aW9uIjoiMjAyNC0wMS0wMVQxMjowMDowMFoiLCJjb25kaXRpb25zIjpbeyJidWNrZXQiOiJ5b3VyLWJ1Y2tldCJ9LHsiayI6InVwbG9hZHMvYXZhdGFyLzEyMy8xNjQwOTk1MjAwMDAwLmpwZyJ9LHsiY29udGVudC1sZW5ndGgtcmFuZ2UiOjAsMTA0ODU3NjBdfV19",
    "qSignAlgorithm": "sha1",
    "qAk": "your-secret-id",
    "qKeyTime": "1640995200;1640998800",
    "qSignKey": "sign-key-123456",
    "qSignature": "signature-123456",
    "objectKey": "uploads/avatar/123/1640995200000.jpg",
    "bucket": "your-bucket",
    "region": "ap-beijing",
    "expire": 1640998800
  }
}
```

#### 错误响应

```json
{
  "code": 1005,
  "message": "获取临时授权失败：错误信息",
  "data": null
}
```

## 返回参数说明

| 参数名         | 类型   | 说明                                  |
| -------------- | ------ | ------------------------------------- |
| sessionToken   | String | 会话令牌，用于临时身份验证            |
| policy         | String | Base64 编码的权限策略                 |
| qSignAlgorithm | String | 签名算法，通常为"sha1"                |
| qAk            | String | 临时访问密钥 ID                       |
| qKeyTime       | String | 密钥有效期，格式为"开始时间;结束时间" |
| qSignKey       | String | 签名密钥                              |
| qSignature     | String | 策略签名                              |
| objectKey      | String | 文件在 COS 中的对象键                 |
| bucket         | String | 存储桶名称                            |
| region         | String | 存储桶所在地域                        |
| expire         | Long   | 过期时间戳                            |

## Policy 格式说明

Policy 是一个 Base64 编码的 JSON 字符串，包含以下内容：

```json
{
  "expiration": "2024-01-01T12:00:00Z",
  "conditions": [
    {
      "bucket": "your-bucket"
    },
    {
      "key": "uploads/avatar/123/1640995200000.jpg"
    },
    {
      "content-length-range": [0, 10485760]
    }
  ]
}
```

## 使用示例

### 前端调用示例

```javascript
// 获取临时授权
const response = await uni.request({
  url: '/api/cos/temp-auth',
  method: 'GET',
  header: {
    Authorization: `Bearer ${uni.getStorageSync('token')}`,
  },
  data: {
    fileName: 'avatar.jpg',
    fileType: 'avatar',
  },
});

if (response.data.code === 200) {
  const authInfo = response.data.data;

  // 使用临时授权信息上传文件
  const uploadResponse = await uni.uploadFile({
    url: `https://${authInfo.bucket}.cos.${authInfo.region}.myqcloud.com`,
    filePath: filePath,
    name: 'file',
    formData: {
      key: authInfo.objectKey,
      policy: authInfo.policy, // 直接使用Base64编码的policy
      'q-sign-algorithm': authInfo.qSignAlgorithm,
      'q-ak': authInfo.qAk,
      'q-key-time': authInfo.qKeyTime,
      'q-signature': authInfo.qSignature,
      'x-cos-security-token': authInfo.sessionToken,
    },
  });
}
```

### cURL 测试示例

```bash
# 获取头像上传临时授权
curl -X GET "http://localhost:8080/api/cos/temp-auth?fileName=avatar.jpg&fileType=avatar" \
  -H "Authorization: Bearer your-jwt-token"

# 获取作品图片上传临时授权
curl -X GET "http://localhost:8080/api/cos/temp-auth?fileName=artwork.jpg&fileType=artwork" \
  -H "Authorization: Bearer your-jwt-token"
```

## 安全特性

1. **临时授权**: 使用临时密钥，有效期 1 小时
2. **用户隔离**: 文件路径包含用户 ID，确保用户间文件隔离
3. **权限控制**: 通过 policy 限制只能上传到指定路径
4. **认证要求**: 需要有效的 JWT token
5. **文件大小限制**: 通过 policy 限制文件大小为 10MB

## 文件类型说明

| fileType  | 说明     | 存储路径                    |
| --------- | -------- | --------------------------- |
| avatar    | 用户头像 | uploads/avatar/{userId}/    |
| artwork   | 作品图片 | uploads/artwork/{userId}/   |
| reference | 参考图片 | uploads/reference/{userId}/ |
| order     | 订单图片 | uploads/order/{userId}/     |

## 注意事项

1. **临时密钥**: 当前实现为模拟，实际使用时需要集成腾讯云 STS 服务
2. **Policy 格式**: Policy 必须是 Base64 编码的 JSON 字符串
3. **文件大小**: 通过 policy 限制上传文件大小为 10MB
4. **文件类型**: 建议只允许图片文件上传
5. **错误处理**: 需要妥善处理上传失败的情况
6. **日志记录**: 建议记录上传操作日志

## 测试

使用提供的测试脚本：

```bash
# 给脚本添加执行权限
chmod +x test_cos_temp_auth.sh

# 运行测试
./test_cos_temp_auth.sh
```

注意：测试前需要先获取有效的 JWT token 并更新脚本中的 TOKEN 变量。
