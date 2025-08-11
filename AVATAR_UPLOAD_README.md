# 头像上传功能说明

## 概述

本功能使用腾讯云 COS（对象存储）实现头像上传，采用临时上传授权的方式，提高安全性和性能。

## 功能特性

- **临时上传授权**：通过预签名 URL 实现安全的直接上传
- **自动文件管理**：自动生成文件路径和文件名
- **用户头像更新**：上传完成后自动更新用户头像
- **文件记录保存**：保存上传记录到数据库
- **兼容性**：保留原有的文件上传接口

## API 接口

### 1. 获取头像上传授权

**接口地址**：`POST /api/user/avatar/upload-policy`

**请求参数**：

```json
{
  "fileName": "avatar.jpg"
}
```

**响应示例**：

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "url": "https://your-bucket.cos.ap-beijing.myqcloud.com/uploads/avatar/123/1640995200000.jpg?sign=xxx",
    "objectKey": "uploads/avatar/123/1640995200000.jpg",
    "expire": 1640998800,
    "bucket": "your-bucket",
    "region": "ap-beijing"
  }
}
```

### 2. 更新用户头像

**接口地址**：`POST /api/user/avatar/update`

**请求参数**：

```json
{
  "objectKey": "uploads/avatar/123/1640995200000.jpg"
}
```

**响应示例**：

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 123,
    "openid": "xxx",
    "nickname": "用户昵称",
    "avatar": "https://your-bucket.cos.ap-beijing.myqcloud.com/uploads/avatar/123/1640995200000.jpg?sign=xxx",
    "phone": "13800138000",
    "balance": 100.0,
    "createTime": "2024-01-01 12:00:00",
    "updateTime": "2024-01-01 12:00:00"
  }
}
```

### 3. 兼容接口（原有）

**接口地址**：`POST /api/user/avatar`

**请求参数**：

- `file`: 头像文件（multipart/form-data）
- `type`: 文件类型（可选，默认为"avatar"）

## 使用流程

### 前端实现步骤

1. **获取上传授权**

   ```javascript
   // 1. 调用获取授权接口
   const response = await uni.request({
     url: '/api/user/avatar/upload-policy',
     method: 'POST',
     header: {
       Authorization: `Bearer ${uni.getStorageSync('token')}`,
     },
     data: {
       fileName: 'avatar.jpg',
     },
   });

   const policy = response.data.data;
   ```

2. **直接上传到 COS**

   ```javascript
   // 2. 使用预签名URL直接上传到腾讯云COS
   const uploadResponse = await uni.uploadFile({
     url: policy.url,
     filePath: filePath,
     name: 'file',
     formData: {
       key: policy.objectKey,
     },
   });
   ```

3. **更新用户头像**
   ```javascript
   // 3. 上传完成后，调用更新接口
   const updateResponse = await uni.request({
     url: '/api/user/avatar/update',
     method: 'POST',
     header: {
       Authorization: `Bearer ${uni.getStorageSync('token')}`,
     },
     data: {
       objectKey: policy.objectKey,
     },
   });
   ```

### 完整示例

```javascript
// 头像上传完整流程
async function uploadAvatar(filePath) {
  try {
    // 1. 获取上传授权
    const policyResponse = await uni.request({
      url: '/api/user/avatar/upload-policy',
      method: 'POST',
      header: {
        Authorization: `Bearer ${uni.getStorageSync('token')}`,
      },
      data: {
        fileName: 'avatar.jpg',
      },
    });

    if (policyResponse.data.code !== 200) {
      throw new Error(policyResponse.data.message);
    }

    const policy = policyResponse.data.data;

    // 2. 上传到腾讯云COS
    const uploadResponse = await uni.uploadFile({
      url: policy.url,
      filePath: filePath,
      name: 'file',
      formData: {
        key: policy.objectKey,
      },
    });

    if (uploadResponse.statusCode !== 200) {
      throw new Error('文件上传失败');
    }

    // 3. 更新用户头像
    const updateResponse = await uni.request({
      url: '/api/user/avatar/update',
      method: 'POST',
      header: {
        Authorization: `Bearer ${uni.getStorageSync('token')}`,
      },
      data: {
        objectKey: policy.objectKey,
      },
    });

    if (updateResponse.data.code === 200) {
      uni.showToast({
        title: '头像上传成功',
        icon: 'success',
      });

      // 更新本地用户信息
      uni.setStorageSync('userInfo', updateResponse.data.data);
    } else {
      throw new Error(updateResponse.data.message);
    }
  } catch (error) {
    uni.showToast({
      title: '头像上传失败：' + error.message,
      icon: 'none',
    });
  }
}
```

## 配置说明

### 腾讯云 COS 配置

在 `application.yml` 中配置腾讯云 COS 参数：

```yaml
# 腾讯云COS配置
tencent:
  cos:
    secret-id: your-secret-id
    secret-key: your-secret-key
    region: ap-beijing
    bucket-name: your-bucket-name
```

### 配置参数说明

- `secret-id`: 腾讯云 API 密钥 ID
- `secret-key`: 腾讯云 API 密钥 Key
- `region`: 存储桶所在地域
- `bucket-name`: 存储桶名称

## 安全说明

1. **临时授权**：使用预签名 URL，有效期 1 小时
2. **用户隔离**：文件路径包含用户 ID，确保用户间文件隔离
3. **文件类型限制**：只支持图片文件
4. **认证要求**：所有接口都需要 JWT token 认证

## 错误处理

### 常见错误码

- `1004`: 用户不存在
- `1005`: 上传失败
- `401`: 未认证或 token 过期

### 错误处理示例

```javascript
// 错误处理
function handleError(error) {
  if (error.code === 401) {
    // token过期，跳转到登录页
    uni.navigateTo({
      url: '/pages/login/login',
    });
  } else {
    uni.showToast({
      title: error.message || '操作失败',
      icon: 'none',
    });
  }
}
```

## 测试

使用提供的测试脚本进行功能测试：

```bash
# 给脚本添加执行权限
chmod +x test_avatar_upload.sh

# 运行测试
./test_avatar_upload.sh
```

注意：测试前需要先获取有效的 JWT token 并更新脚本中的 TOKEN 变量。
