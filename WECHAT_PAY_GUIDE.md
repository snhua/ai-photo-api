# 微信支付功能实现指南

## 功能概述

本项目实现了完整的微信支付功能，包括：

- 创建支付订单
- 微信支付统一下单
- 支付状态查询
- 支付回调处理
- 订单取消
- 退款功能

## 技术架构

### 1. 核心组件

- **PaymentController**: 支付控制器，提供 REST API 接口
- **PaymentService**: 支付业务逻辑服务
- **WechatPayService**: 微信支付专用服务
- **Payment**: 支付记录实体类
- **WechatPayConfig**: 微信支付配置类

### 2. 数据库设计

```sql
CREATE TABLE `payments` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '支付记录ID',
  `order_id` bigint(20) NOT NULL COMMENT '订单ID',
  `payment_no` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '支付单号',
  `amount_yuan` decimal(10,2) DEFAULT NULL COMMENT '支付金额',
  `payment_method` enum('wechat','balance') COLLATE utf8mb4_unicode_ci DEFAULT 'wechat' COMMENT '支付方式',
  `status` enum('pending','success','failed','refunded') COLLATE utf8mb4_unicode_ci DEFAULT 'pending' COMMENT '支付状态',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `order_no` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `user_id` bigint(20) NOT NULL,
  `amount` int(11) NOT NULL,
  `description` varchar(128) COLLATE utf8mb4_unicode_ci NOT NULL,
  `client_ip` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `remark` varchar(128) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `payment_no` (`payment_no`),
  KEY `idx_payment_no` (`payment_no`),
  KEY `idx_order_id` (`order_id`),
  KEY `idx_status` (`status`),
  KEY `idx_created_at` (`create_time`),
  KEY `idx_payments_order_status` (`order_id`,`status`),
  CONSTRAINT `payments_ibfk_1` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='支付记录表';
```

## 配置说明

### 1. 微信支付配置

在 `application.yml` 中配置微信支付参数：

```yaml
wechat:
  pay:
    app-id: your-app-id # 微信小程序AppID
    mch-id: your-mch-id # 商户号
    mch-key: your-mch-key # 商户API密钥
    cert-path: /path/to/your/cert.p12 # 商户证书路径
    cert-password: your-cert-password # 商户证书密码
    notify-url: https://your-domain.com/payment/wechat/notify # 支付回调地址
    refund-notify-url: https://your-domain.com/payment/wechat/refund-notify # 退款回调地址
    sandbox: false # 是否沙箱环境
    sandbox-key: your-sandbox-key # 沙箱环境密钥
    timeout-minutes: 30 # 支付超时时间（分钟）
```

### 2. 微信商户平台配置

1. **登录微信商户平台**
2. **配置 API 密钥**
3. **上传 API 证书**
4. **配置回调地址**：
   - 支付结果通知：`https://your-domain.com/payment/wechat/notify`
   - 退款结果通知：`https://your-domain.com/payment/wechat/refund-notify`

## API 接口说明

### 1. 创建支付订单

**接口地址**: `POST /payment/create`

**请求参数**:

```json
{
  "orderNo": "ORDER202312010001",
  "amount": 99.99,
  "paymentMethod": "WECHAT",
  "description": "AI绘画服务费用",
  "clientIp": "127.0.0.1",
  "remark": "用户备注"
}
```

**响应示例**:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "paymentNo": "PAY202312010001",
    "orderNo": "ORDER202312010001",
    "amount": 99.99,
    "paymentMethod": "WECHAT",
    "status": "PENDING",
    "wechatPayParams": {
      "appId": "wx8888888888888888",
      "timeStamp": "1414561699",
      "nonceStr": "5K8264ILTKCH16CQ2502SI8ZNMTM67VS",
      "prepayId": "prepay_id=wx201410272009395522657a690389285100",
      "signType": "RSA",
      "paySign": "oR9d8PuhnIc+YZ8cBHFCwfgpaK9gd7vaRvkYD7rthRAZ..."
    }
  }
}
```

### 2. 查询支付订单

**接口地址**: `GET /payment/{paymentNo}`

**响应示例**:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "orderId": 123,
    "paymentNo": "PAY202312010001",
    "orderNo": "ORDER202312010001",
    "userId": 123,
    "amount": 9999,
    "amountYuan": 99.99,
    "paymentMethod": "wechat",
    "status": "success",
    "description": "AI绘画服务费用",
    "clientIp": "127.0.0.1",
    "createTime": "2023-12-01T10:25:00",
    "updateTime": "2023-12-01T10:30:00"
  }
}
```

### 3. 查询支付状态

**接口地址**: `GET /payment/{paymentNo}/status`

**响应示例**:

```json
{
  "code": 200,
  "message": "success",
  "data": "success"
}
```

### 4. 查询用户支付记录

**接口地址**: `GET /payment/user?current=1&size=10`

**响应示例**:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "records": [
      {
        "id": 1,
        "orderId": 123,
        "paymentNo": "PAY202312010001",
        "orderNo": "ORDER202312010001",
        "amount": 9999,
        "amountYuan": 99.99,
        "paymentMethod": "wechat",
        "status": "success",
        "createTime": "2023-12-01T10:25:00"
      }
    ],
    "total": 1,
    "size": 10,
    "current": 1,
    "pages": 1
  }
}
```

### 5. 取消支付订单

**接口地址**: `POST /payment/{paymentNo}/cancel`

**响应示例**:

```json
{
  "code": 200,
  "message": "success",
  "data": true
}
```

### 6. 申请退款

**接口地址**: `POST /payment/{paymentNo}/refund?amount=9999&reason=用户申请退款`

**响应示例**:

```json
{
  "code": 200,
  "message": "success",
  "data": true
}
```

## 小程序端集成

### 1. 调用支付接口

```javascript
// 1. 调用后端创建支付订单
const response = await wx.request({
  url: 'https://your-domain.com/payment/create',
  method: 'POST',
  header: {
    Authorization: `Bearer ${token}`,
    'Content-Type': 'application/json',
  },
  data: {
    orderNo: 'ORDER202312010001',
    amount: 99.99,
    paymentMethod: 'WECHAT',
    description: 'AI绘画服务费用',
  },
});

// 2. 调用微信支付
if (response.data.code === 200) {
  const payParams = response.data.data.wechatPayParams;
  wx.requestPayment({
    timeStamp: payParams.timeStamp,
    nonceStr: payParams.nonceStr,
    package: payParams.prepayId,
    signType: payParams.signType,
    paySign: payParams.paySign,
    success: function (res) {
      console.log('支付成功', res);
      // 查询支付状态
      checkPaymentStatus(response.data.data.paymentNo);
    },
    fail: function (res) {
      console.log('支付失败', res);
    },
  });
}
```

### 2. 查询支付状态

```javascript
function checkPaymentStatus(paymentNo) {
  wx.request({
    url: `https://your-domain.com/payment/${paymentNo}/status`,
    method: 'GET',
    header: {
      Authorization: `Bearer ${token}`,
    },
    success: function (res) {
      if (res.data.code === 200) {
        const status = res.data.data;
        if (status === 'success') {
          console.log('支付成功');
        } else if (status === 'pending') {
          console.log('支付中');
          // 轮询查询
          setTimeout(() => checkPaymentStatus(paymentNo), 2000);
        } else {
          console.log('支付失败');
        }
      }
    },
  });
}
```

## 支付流程

### 1. 创建支付订单流程

1. 用户在小程序中选择商品并点击支付
2. 小程序调用后端 `/payment/create` 接口
3. 后端生成支付订单号，调用微信统一下单接口
4. 微信返回预支付 ID 和支付参数
5. 后端返回支付参数给小程序
6. 小程序调用 `wx.requestPayment` 发起支付

### 2. 支付回调处理流程

1. 用户完成支付后，微信向商户服务器发送支付结果通知
2. 商户服务器接收回调数据，验证签名
3. 解析回调数据，更新支付订单状态
4. 处理业务逻辑（如更新订单状态）
5. 返回处理结果给微信

### 3. 支付状态查询流程

1. 小程序支付完成后，调用后端查询接口
2. 后端查询本地数据库获取支付状态
3. 如果状态为待支付，可调用微信查询接口确认
4. 返回支付状态给小程序

## 安全注意事项

### 1. 签名验证

- 所有微信支付回调必须验证签名
- 使用商户 API 密钥进行签名验证
- 防止恶意回调攻击

### 2. 金额验证

- 支付金额必须精确到分
- 验证支付金额与订单金额是否一致
- 防止金额篡改

### 3. 订单号唯一性

- 支付订单号必须全局唯一
- 防止重复支付
- 使用时间戳+随机数生成

### 4. 状态管理

- 支付状态变更必须严格按流程
- 防止状态混乱
- 记录状态变更日志

## 数据库字段说明

### 支付状态 (status)

- `pending`: 待支付
- `success`: 支付成功
- `failed`: 支付失败
- `refunded`: 已退款

### 支付方式 (payment_method)

- `wechat`: 微信支付
- `balance`: 余额支付

## 测试指南

### 1. 使用测试脚本

```bash
# 运行测试脚本
./test_payment.sh
```

### 2. 沙箱环境测试

1. 在微信商户平台申请沙箱环境
2. 配置沙箱环境的 API 密钥
3. 使用沙箱环境的测试账号进行支付测试

### 3. 生产环境测试

1. 使用真实的小程序账号
2. 配置生产环境的 API 密钥和证书
3. 使用真实金额进行测试（建议小额测试）

## 常见问题

### 1. 支付失败

- 检查微信支付配置是否正确
- 验证 API 密钥和证书是否有效
- 确认回调地址是否可访问

### 2. 回调处理失败

- 检查回调地址是否正确
- 验证签名算法是否正确
- 确认服务器防火墙设置

### 3. 支付状态不同步

- 检查回调处理逻辑
- 验证数据库更新是否成功
- 确认网络连接是否稳定

## 相关文件

- `PaymentController.java`: 支付控制器
- `PaymentService.java`: 支付服务接口
- `PaymentServiceImpl.java`: 支付服务实现
- `WechatPayService.java`: 微信支付服务接口
- `WechatPayServiceImpl.java`: 微信支付服务实现
- `Payment.java`: 支付实体类
- `WechatPayConfig.java`: 微信支付配置
- `payment_table.sql`: 数据库表结构
- `test_payment.sh`: 测试脚本
