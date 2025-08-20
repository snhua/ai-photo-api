# 微信商家转账 API 实现总结

## 主要变更

### 1. API 接口升级

- **原接口**：V2 企业付款到零钱
- **新接口**：V3 商家转账 API

### 2. 请求格式变更

- **原格式**：XML 格式 + MD5 签名
- **新格式**：JSON 格式 + RSA-SHA256 签名

### 3. 实现文件

#### 配置类更新

- `WechatPayConfig.java` - 添加 serialNo 和 privateKeyPath 字段

#### 服务实现更新

- `WechatPayServiceImpl.java` - 重写 transferToBalance 和 queryTransferToBalance 方法
- 新增 V3 签名方法 generateV3Signature
- 新增 HTTP 请求方法 sendV3Request

#### 业务逻辑更新

- `WalletServiceImpl.java` - 更新状态判断逻辑

### 4. 测试文件

- `test_wechat_transfer_api.sh` - 完整的 API 测试脚本
- `WECHAT_TRANSFER_API_GUIDE.md` - 配置指南

## 关键特性

1. **支持转账场景**：佣金报酬场景
2. **实名校验**：超过 2000 元必须实名校验
3. **报备信息**：符合微信支付要求
4. **错误处理**：完善的错误信息处理
5. **状态管理**：支持多种转账状态

## 配置要求

```yaml
wechat:
  pay:
    appId: your_app_id
    mchId: your_mch_id
    apiV3Key: your_api_v3_key
    serialNo: your_serial_no
    privateKeyPath: /path/to/private_key.pem
```

## 测试验证

运行测试脚本：

```bash
./test_wechat_transfer_api.sh
```

## 注意事项

1. 需要配置正确的证书文件
2. 服务器 IP 需要在微信支付白名单中
3. 金额限制：单笔最大 2000 元，每日最大 10000 元
4. 最小金额：0.3 元

## 总结

成功将微信支付接口从 V2 升级到 V3，提供更安全、更规范的转账服务。
