# 充值流程修正文档

## 概述

本文档描述了修正后的充值流程，实现了完整的微信支付充值功能，包括前端发起充值、后台创建支付订单、用户微信支付、后端回调处理等完整流程。

## 修正后的充值流程

### 1. 用户发起充值

1. 用户在前端钱包页面点击充值按钮
2. 输入充值金额（前端验证金额有效性）
3. 前端调用创建充值支付订单接口

### 2. 后台创建支付订单

1. 后端接收充值请求，验证参数
2. 生成充值订单号（格式：RECHARGE + 时间戳 + 随机数）
3. 调用支付服务创建微信支付订单
4. 记录充值交易记录（状态为 pending）
5. 返回微信支付参数给前端

### 3. 用户微信支付

1. 前端接收支付参数
2. 调用微信支付 SDK 发起支付
3. 用户在微信中完成支付

### 4. 微信支付回调

1. 微信服务器向我们的回调接口发送支付结果
2. 后端验证回调签名
3. 解析支付结果数据
4. 判断是否为充值支付（通过订单号前缀 RECHARGE 识别）
5. 调用钱包服务处理充值成功逻辑

### 5. 充值成功处理

1. 更新用户余额
2. 更新充值交易记录状态为 completed
3. 更新用户总收入统计
4. 记录详细的日志信息

### 6. 前端状态查询

1. 前端支付成功后轮询查询支付状态
2. 确认充值成功后刷新钱包信息
3. 显示充值成功提示

## 技术实现

### 后端接口修改

#### 1. 钱包控制器 (WalletController)

- 修改 `/api/wallet/recharge` 接口，改为创建充值支付订单
- 新增 `/api/wallet/recharge/{paymentNo}/status` 接口，查询充值支付状态

#### 2. 钱包服务 (WalletService)

- 新增 `createRechargeOrder()` 方法：创建充值支付订单
- 新增 `getRechargeStatus()` 方法：查询充值支付状态
- 新增 `handleRechargePaymentSuccess()` 方法：处理充值支付成功回调

#### 3. 支付服务 (PaymentService)

- 修改微信支付回调处理逻辑
- 添加充值支付识别和处理逻辑
- 区分充值支付和订单支付的处理流程

### 前端代码修改

#### 1. API 接口

- 修改 `recharge()` 为 `createRechargeOrder()`
- 新增 `getRechargeStatus()` 接口

#### 2. 钱包页面

- 修改充值逻辑，支持微信支付流程
- 添加支付状态轮询功能
- 优化用户体验和错误处理

## 数据库设计

### 1. 支付记录表 (payments)

- 记录所有支付订单信息
- 包含充值支付和订单支付

### 2. 钱包交易记录表 (wallet_transactions)

- 记录充值交易记录
- 关联支付订单号
- 状态：pending → completed

### 3. 用户表 (users)

- 存储用户余额和统计信息
- 支付成功后更新余额

## 关键代码实现

### 1. 创建充值支付订单

```java
@Override
@Transactional
public Map<String, Object> createRechargeOrder(Long userId, RechargeRequestDTO request, String clientIp) {
    // 生成充值订单号
    String rechargeOrderNo = generateRechargeOrderNo();

    // 创建支付请求
    PaymentRequest paymentRequest = new PaymentRequest();
    paymentRequest.setOrderNo(rechargeOrderNo);
    paymentRequest.setAmount(request.getAmount());
    paymentRequest.setPaymentMethod("WECHAT");
    paymentRequest.setDescription("钱包充值 - " + request.getAmount() + "元");
    paymentRequest.setClientIp(clientIp);
    paymentRequest.setRemark("用户钱包充值");

    // 创建支付订单
    PaymentResponse paymentResponse = paymentService.createPayment(paymentRequest, userId);

    // 记录充值订单
    WalletTransaction transaction = new WalletTransaction();
    transaction.setUserId(userId);
    transaction.setType("income");
    transaction.setAmount(request.getAmount());
    transaction.setRelatedType("recharge");
    transaction.setRelatedId(paymentResponse.getPaymentNo());
    transaction.setStatus("pending");
    walletTransactionMapper.insert(transaction);

    // 返回支付参数
    Map<String, Object> result = new HashMap<>();
    result.put("paymentNo", paymentResponse.getPaymentNo());
    result.put("rechargeOrderNo", rechargeOrderNo);
    result.put("amount", request.getAmount());
    result.put("wechatPayParams", paymentResponse.getWechatPayParams());

    return result;
}
```

### 2. 处理充值支付成功回调

```java
@Override
@Transactional
public boolean handleRechargePaymentSuccess(String paymentNo, String transactionId) {
    // 查询支付记录
    Payment payment = paymentService.getPaymentByNo(paymentNo);
    if (payment == null) {
        return false;
    }

    // 检查是否已经处理过
    WalletTransaction existingTransaction = walletTransactionMapper.selectOne(wrapper);
    if (existingTransaction != null && "completed".equals(existingTransaction.getStatus())) {
        return true;
    }

    // 更新用户余额
    boolean success = updateUserBalance(payment.getUserId(), payment.getAmountYuan());
    if (!success) {
        return false;
    }

    // 更新交易记录状态
    WalletTransaction transaction = walletTransactionMapper.selectOne(wrapper);
    if (transaction != null) {
        transaction.setStatus("completed");
        transaction.setTransactionNo(transactionId);
        walletTransactionMapper.updateById(transaction);
    }

    // 更新用户总收入
    userMapper.updateUserStatistics(payment.getUserId(), payment.getAmountYuan(), BigDecimal.ZERO);

    return true;
}
```

### 3. 前端微信支付处理

```javascript
// 处理微信支付
const handleWechatPay = async (paymentData) => {
  const wechatPayParams = paymentData.wechatPayParams;

  uni.requestPayment({
    provider: 'wxpay',
    timeStamp: wechatPayParams.timeStamp,
    nonceStr: wechatPayParams.nonceStr,
    package: wechatPayParams.package,
    signType: wechatPayParams.signType,
    paySign: wechatPayParams.paySign,
    success: async (res) => {
      // 支付成功后轮询查询支付状态
      await pollPaymentStatus(paymentData.paymentNo);
    },
    fail: (err) => {
      uni.showToast({
        title: '支付失败',
        icon: 'none',
      });
    },
  });
};

// 轮询查询支付状态
const pollPaymentStatus = async (paymentNo) => {
  const poll = async () => {
    const response = await getRechargeStatus(paymentNo);
    const status = response.data;

    if (status === 'completed' || status === 'success') {
      // 支付成功，刷新钱包信息
      uni.showToast({
        title: '充值成功',
        icon: 'success',
      });
      await loadWalletInfo();
      return;
    }

    // 继续轮询
    setTimeout(poll, 2000);
  };

  poll();
};
```

## 安全考虑

### 1. 支付安全

- 验证微信支付回调签名
- 防止重复处理支付回调
- 金额验证和限制

### 2. 数据安全

- 使用事务确保数据一致性
- 详细的日志记录
- 异常处理和回滚机制

### 3. 用户体验

- 支付状态实时查询
- 友好的错误提示
- 支付超时处理

## 日志记录

### 1. 关键操作日志

- 创建充值订单
- 支付回调处理
- 余额更新操作
- 异常情况记录

### 2. 日志格式

```
[时间] [级别] [操作] [用户ID] [订单号] [金额] [状态] [详情]
```

## 测试验证

### 1. 功能测试

- 充值订单创建
- 微信支付流程
- 回调处理逻辑
- 余额更新验证

### 2. 异常测试

- 网络异常处理
- 支付失败处理
- 重复回调处理
- 数据一致性验证

## 部署注意事项

### 1. 配置要求

- 微信支付配置
- 回调地址配置
- 数据库连接配置

### 2. 监控要求

- 支付成功率监控
- 回调处理监控
- 余额变化监控
- 异常告警配置

## 总结

修正后的充值流程实现了完整的微信支付集成，提供了安全、可靠、用户友好的充值体验。通过合理的架构设计和详细的日志记录，确保了系统的稳定性和可维护性。
