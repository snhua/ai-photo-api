# 腾讯云 STS 配置指南

## 概述

为了安全地使用腾讯云 COS 的临时上传功能，需要配置腾讯云 STS（Security Token Service）服务。STS 可以生成临时访问密钥，提供更安全的文件上传方式。

## 配置步骤

### 1. 创建 CAM 角色

1. 登录腾讯云控制台
2. 进入 CAM（访问管理）服务
3. 创建自定义策略：

```json
{
  "version": "2.0",
  "statement": [
    {
      "effect": "allow",
      "action": ["cos:PutObject", "cos:GetObject", "cos:DeleteObject"],
      "resource": ["qcs::cos:ap-guangzhou:uid/*:*/aiphoto-1304396619/uploads/*"]
    }
  ]
}
```

4. 创建角色：
   - 角色类型：腾讯云账号
   - 角色名称：COSAccessRole
   - 关联策略：选择上面创建的自定义策略

### 2. 获取角色 ARN

创建角色后，获取角色的 ARN，格式如下：

```
qcs::cam::uin/100012345678:roleName/COSAccessRole
```

### 3. 配置应用

在 `application.yml` 中配置 STS 相关参数：

```yaml
# 腾讯云COS配置
tencent:
  cos:
    secret-id: your-secret-id
    secret-key: your-secret-key
    region: ap-guangzhou
    bucket-name: aiphoto-1304396619
    role-arn: qcs::cam::uin/100012345678:roleName/COSAccessRole
    role-session-name: cos-upload-session
```

### 4. 权限说明

配置的权限允许：

- 上传文件到指定路径
- 获取文件
- 删除文件
- 限制在特定存储桶和路径下操作

## 安全特性

1. **临时密钥**: 通过 STS 获取临时访问密钥，有效期 1 小时
2. **权限最小化**: 只授予必要的 COS 操作权限
3. **路径限制**: 限制只能操作 uploads 目录下的文件
4. **用户隔离**: 文件路径包含用户 ID，确保用户间文件隔离

## 测试配置

使用测试脚本验证 STS 配置：

```bash
# 运行测试
./test_cos_temp_auth.sh
```

## 注意事项

1. **角色 ARN**: 确保角色 ARN 格式正确
2. **权限策略**: 根据实际需求调整权限策略
3. **密钥安全**: 不要在代码中硬编码密钥信息
4. **错误处理**: 妥善处理 STS 调用失败的情况

## 故障排除

### 常见错误

1. **InvalidRoleArn**: 角色 ARN 格式错误
2. **AccessDenied**: 权限不足
3. **RoleNotExist**: 角色不存在

### 解决方案

1. 检查角色 ARN 格式
2. 确认角色权限配置
3. 验证密钥信息正确性
4. 检查网络连接

## 生产环境建议

1. **密钥管理**: 使用腾讯云密钥管理服务
2. **监控告警**: 设置 STS 调用监控
3. **日志记录**: 记录所有 STS 操作日志
4. **定期轮换**: 定期更新访问密钥
