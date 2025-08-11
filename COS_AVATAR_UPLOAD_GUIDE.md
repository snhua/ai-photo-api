# 腾讯云 COS 头像上传功能使用指南

## 功能概述

已成功实现基于腾讯云 COS 的头像上传功能，采用临时上传授权方式，提供安全、高效的文件上传解决方案。

## 已实现的功能

### 1. 核心组件

- ✅ **CosConfig**: 腾讯云 COS 配置类
- ✅ **CosService**: COS 服务接口
- ✅ **CosServiceImpl**: COS 服务实现类
- ✅ **UserController**: 用户控制器（已集成 COS 功能）
- ✅ **FileUploadService**: 文件上传服务（兼容性支持）
- ✅ **UserContext**: 用户上下文工具类

### 2. API 接口

#### 获取上传授权

```
POST /api/user/avatar/upload-policy
Content-Type: application/x-www-form-urlencoded
Authorization: Bearer {token}

参数: fileName=avatar.jpg
```

#### 更新用户头像

```
POST /api/user/avatar/update
Content-Type: application/x-www-form-urlencoded
Authorization: Bearer {token}

参数: objectKey=uploads/avatar/123/1640995200000.jpg
```

#### 兼容接口（原有）

```
POST /api/user/avatar
Content-Type: multipart/form-data
Authorization: Bearer {token}

参数: file={文件}, type=avatar
```

### 3. 配置要求

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

## 前端使用流程

### 1. 获取上传授权

```javascript
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

### 2. 直接上传到 COS

```javascript
const uploadResponse = await uni.uploadFile({
  url: policy.url,
  filePath: filePath,
  name: 'file',
  formData: {
    key: policy.objectKey,
  },
});
```

### 3. 更新用户头像

```javascript
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

## 安全特性

1. **临时授权**: 使用预签名 URL，有效期 1 小时
2. **用户隔离**: 文件路径包含用户 ID，确保用户间文件隔离
3. **认证要求**: 所有接口都需要 JWT token 认证
4. **文件类型限制**: 只支持图片文件

## 测试

使用提供的测试脚本：

```bash
# 运行测试
./test_avatar_upload.sh
```

注意：测试前需要先获取有效的 JWT token 并更新脚本中的 TOKEN 变量。

## 文件结构

```
api/
├── src/main/java/com/aiphone/
│   ├── config/
│   │   └── CosConfig.java                    # COS配置类
│   ├── controller/
│   │   └── UserController.java               # 用户控制器（已集成COS）
│   ├── service/
│   │   ├── CosService.java                   # COS服务接口
│   │   ├── FileUploadService.java            # 文件上传服务接口
│   │   └── impl/
│   │       ├── CosServiceImpl.java           # COS服务实现
│   │       └── FileUploadServiceImpl.java    # 文件上传服务实现
│   └── util/
│       └── UserContext.java                  # 用户上下文工具类
├── test_avatar_upload.sh                     # 测试脚本
├── AVATAR_UPLOAD_README.md                  # 详细文档
└── COS_AVATAR_UPLOAD_GUIDE.md              # 本指南
```

## 下一步

1. 配置腾讯云 COS 的实际参数
2. 测试完整的上传流程
3. 根据实际需求调整文件路径和命名规则
4. 添加更多的错误处理和日志记录

## 注意事项

- 确保腾讯云 COS 的配置正确
- 测试时需要使用有效的 JWT token
- 文件上传大小限制为 10MB
- 只支持图片文件格式
