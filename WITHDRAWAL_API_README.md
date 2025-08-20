# 提现功能 API 文档

## 概述

本系统实现了基于微信企业付款到零钱的提现功能，用户可以将钱包中的可提现金额提现到微信零钱。

## 功能特性

- ✅ 提现申请（支持金额验证、手续费计算）
- ✅ 微信企业付款到零钱
- ✅ 提现状态查询
- ✅ 提现记录管理
- ✅ 交易记录追踪
- ✅ 自动退款机制（提现失败时）

## API 接口

### 1. 申请提现

**接口地址：** `POST /api/wallet/withdraw`

**请求参数：**

```json
{
  "amount": 10.0, // 提现金额（元）
  "paymentMethod": "wechat", // 提现方式
  "paymentAccount": "openid", // 收款账户（微信openid）
  "remark": "提现备注" // 备注信息
}
```

**响应示例：**

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "userId": 123,
    "withdrawalNo": "WD202312011234567890",
    "amount": 10.0,
    "fee": 0.2,
    "actualAmount": 9.8,
    "paymentMethod": "wechat",
    "paymentAccount": "openid",
    "status": "pending",
    "remark": "提现备注",
    "createdAt": "2023-12-01T12:34:56"
  }
}
```

### 2. 查询提现状态

**接口地址：** `GET /api/wallet/withdrawal/{withdrawalNo}/status`

**响应示例：**

```json
{
  "code": 200,
  "message": "success",
  "data": "success" // pending, processing, success, failed
}
```

### 3. 获取提现记录

**接口地址：** `GET /api/wallet/withdrawal-history`

**请求参数：**

- `page`: 页码（默认 1）
- `pageSize`: 每页大小（默认 10）
- `status`: 状态筛选（可选）

**响应示例：**

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "records": [
      {
        "id": 1,
        "withdrawalNo": "WD202312011234567890",
        "amount": 10.0,
        "fee": 0.2,
        "actualAmount": 9.8,
        "status": "success",
        "createdAt": "2023-12-01T12:34:56",
        "processedAt": "2023-12-01T12:35:00"
      }
    ],
    "total": 1,
    "size": 10,
    "current": 1
  }
}
```

### 4. 处理提现申请（管理员接口）

**接口地址：** `POST /api/wallet/process-withdrawal/{withdrawalId}`

**响应示例：**

```json
{
  "code": 200,
  "message": "success",
  "data": true
}
```

## 微信企业付款配置

### 配置要求

1. **商户号配置**

   - 需要开通微信企业付款到零钱功能
   - 配置商户 API 证书
   - 设置 API 密钥

2. **配置文件**
   ```yaml
   wechat:
     pay:
       appId: your_app_id
       mchId: your_mch_id
       mchKey: your_mch_key
       privateKeyPath: /path/to/apiclient_key.pem
       publicKeyPath: /path/to/apiclient_public_key.pem
       apiV3Key: your_api_v3_key
   ```

### 企业付款参数

- **接口地址：** `https://api.mch.weixin.qq.com/mmpaymkttransfers/promotion/transfers`
- **查询接口：** `https://api.mch.weixin.qq.com/mmpaymkttransfers/gettransferinfo`
- **金额单位：** 分（需要将元转换为分）
- **校验姓名：** 默认不校验（NO_CHECK）

## 业务逻辑

### 提现流程

1. **申请阶段**

   - 验证用户可提现金额
   - 检查提现金额限制（最小/最大金额）
   - 检查每日提现限额
   - 计算手续费
   - 创建提现申请记录
   - 扣除可提现金额

2. **处理阶段**

   - 验证用户 openid
   - 调用微信企业付款接口
   - 更新提现状态
   - 记录交易流水

3. **完成阶段**
   - 提现成功：更新状态为 success
   - 提现失败：更新状态为 failed，退还可提现金额

### 状态说明

- `pending`: 待处理
- `processing`: 处理中
- `success`: 成功
- `failed`: 失败
- `cancelled`: 已取消

### 错误处理

- **余额不足：** 返回错误码 2002
- **金额超限：** 返回错误码 2003
- **超出限额：** 返回错误码 2004
- **状态错误：** 返回错误码 2005
- **未绑定微信：** 返回错误码 2006
- **处理失败：** 返回错误码 2007

## 安全考虑

1. **金额验证**

   - 服务端验证提现金额
   - 防止超额提现

2. **频率限制**

   - 每日提现次数限制
   - 每日提现金额限制

3. **状态管理**

   - 防止重复处理
   - 状态变更记录

4. **异常处理**
   - 网络异常重试
   - 失败自动退款

## 测试

### 测试脚本

使用提供的测试脚本进行功能验证：

```bash
./test_withdrawal_api.sh
```

### 测试场景

1. **正常提现**

   - 申请提现
   - 处理提现
   - 查询状态

2. **异常场景**
   - 余额不足
   - 金额超限
   - 网络异常
   - 微信接口异常

## 注意事项

1. **微信企业付款限制**

   - 单笔最大金额：2000 元
   - 每日最大金额：10000 元
   - 需要用户实名认证

2. **手续费计算**

   - 按比例收取手续费
   - 设置最小和最大手续费

3. **状态同步**

   - 定期查询微信接口状态
   - 处理异步回调

4. **日志记录**
   - 记录所有提现操作
   - 保存微信接口响应

## 相关文件

- `WalletService.java` - 钱包服务接口
- `WalletServiceImpl.java` - 钱包服务实现
- `WechatPayService.java` - 微信支付服务接口
- `WechatPayServiceImpl.java` - 微信支付服务实现
- `WithdrawalRequest.java` - 提现申请实体
- `WalletController.java` - 钱包控制器
