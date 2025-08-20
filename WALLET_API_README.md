# 钱包功能 API 实现说明

## 概述

本文档描述了 AI 绘画师小程序钱包功能的完整 API 实现，包括余额管理、充值、提现、延迟到账等功能。

## 功能特性

### 核心功能

- **余额管理**：用户账户余额，可用于支付订单
- **可提现金额**：画师完成订单后可提现的金额
- **技术服务费**：平台收取 20% 的技术服务费
- **延迟到账**：可提现金额延后 7 天到账，确保订单质量
- **交易记录**：完整的钱包交易日志记录

### 业务流程

1. 用户充值 → 余额增加
2. 用户下单 → 从余额扣除订单金额
3. 画师完成订单 → 订单金额的 80% 立即到账（余额），20% 技术服务费扣除
4. 用户确认收货 → 订单金额的 80% 延后 7 天转为可提现金额
5. 画师提现 → 从可提现金额扣除

## 数据库设计

### 新增表结构

#### 1. 延迟到账记录表 (wallet_pending_withdrawals)

```sql
CREATE TABLE `wallet_pending_withdrawals` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '记录ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `order_id` BIGINT NOT NULL COMMENT '订单ID',
    `amount` DECIMAL(10,2) NOT NULL COMMENT '金额',
    `service_fee` DECIMAL(10,2) NOT NULL COMMENT '技术服务费',
    `available_at` TIMESTAMP NOT NULL COMMENT '可提现时间',
    `status` ENUM('pending', 'available', 'withdrawn', 'cancelled') DEFAULT 'pending' COMMENT '状态',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
);
```

#### 2. 提现申请表 (withdrawal_requests)

```sql
CREATE TABLE `withdrawal_requests` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '提现申请ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `withdrawal_no` VARCHAR(50) UNIQUE NOT NULL COMMENT '提现单号',
    `amount` DECIMAL(10,2) NOT NULL COMMENT '提现金额',
    `fee` DECIMAL(10,2) DEFAULT 0.00 COMMENT '手续费',
    `actual_amount` DECIMAL(10,2) NOT NULL COMMENT '实际到账金额',
    `payment_method` ENUM('wechat', 'alipay', 'bank') NOT NULL COMMENT '提现方式',
    `payment_account` VARCHAR(100) COMMENT '收款账户',
    `status` ENUM('pending', 'processing', 'success', 'failed', 'cancelled') DEFAULT 'pending' COMMENT '提现状态',
    `remark` VARCHAR(500) COMMENT '备注',
    `processed_at` TIMESTAMP NULL COMMENT '处理时间',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
);
```

### 扩展表结构

#### 1. 用户表扩展 (users)

```sql
ALTER TABLE `users`
ADD COLUMN `withdrawable_balance` DECIMAL(10,2) DEFAULT 0.00 COMMENT '可提现金额',
ADD COLUMN `total_income` DECIMAL(10,2) DEFAULT 0.00 COMMENT '总收入',
ADD COLUMN `total_withdraw` DECIMAL(10,2) DEFAULT 0.00 COMMENT '总提现',
ADD COLUMN `service_fee_total` DECIMAL(10,2) DEFAULT 0.00 COMMENT '技术服务费总额';
```

#### 2. 钱包交易记录表扩展 (wallet_transactions)

```sql
ALTER TABLE `wallet_transactions`
ADD COLUMN `withdrawable_amount` DECIMAL(10,2) DEFAULT 0.00 COMMENT '可提现金额变动',
ADD COLUMN `service_fee` DECIMAL(10,2) DEFAULT 0.00 COMMENT '技术服务费',
ADD COLUMN `available_at` TIMESTAMP NULL COMMENT '可提现时间（延迟到账）',
ADD COLUMN `transaction_no` VARCHAR(50) UNIQUE COMMENT '交易流水号',
ADD COLUMN `status` ENUM('pending', 'completed', 'failed', 'cancelled') DEFAULT 'completed' COMMENT '交易状态';
```

## 代码结构

### 实体类 (Entity)

- `User.java` - 用户实体（已扩展钱包字段）
- `WalletTransaction.java` - 钱包交易记录实体（已扩展）
- `WalletPendingWithdrawal.java` - 延迟到账记录实体
- `WithdrawalRequest.java` - 提现申请实体

### DTO 类

- `WalletInfo.java` - 钱包信息 DTO
- `WalletTransactionDTO.java` - 钱包交易记录 DTO
- `WithdrawalRequestDTO.java` - 提现申请 DTO
- `RechargeRequestDTO.java` - 充值请求 DTO

### Mapper 接口

- `UserMapper.java` - 用户数据访问（已扩展钱包方法）
- `WalletTransactionMapper.java` - 钱包交易记录数据访问（已扩展）
- `WalletPendingWithdrawalMapper.java` - 延迟到账记录数据访问
- `WithdrawalRequestMapper.java` - 提现申请数据访问

### 服务层

- `WalletService.java` - 钱包服务接口
- `WalletServiceImpl.java` - 钱包服务实现

### 控制器层

- `WalletController.java` - 钱包控制器

### 定时任务

- `WalletScheduledTasks.java` - 钱包定时任务

## API 接口

### 1. 获取钱包信息

```
GET /api/wallet
```

### 2. 充值

```
POST /api/wallet/recharge
Content-Type: application/json

{
  "amount": 100.00
}
```

### 3. 提现申请

```
POST /api/wallet/withdraw
Content-Type: application/json

{
  "amount": 100.00,
  "paymentMethod": "wechat",
  "paymentAccount": "13800138000",
  "remark": "提现到微信"
}
```

### 4. 获取交易记录

```
GET /api/wallet/transactions?page=1&pageSize=20&type=income&relatedType=order_income
```

### 5. 获取提现记录

```
GET /api/wallet/withdrawals?page=1&pageSize=20&status=pending
```

### 6. 获取延迟到账记录

```
GET /api/wallet/pending-withdrawals?page=1&pageSize=20&status=pending
```

### 7. 获取钱包配置

```
GET /api/wallet/config
```

### 8. 获取钱包统计

```
GET /api/wallet/statistics?period=month
```

### 9. 处理订单收入（内部接口）

```
POST /api/wallet/process-order-income/{orderId}
```

### 10. 处理延迟到账（定时任务）

```
POST /api/wallet/process-pending-withdrawals
```

## 部署步骤

### 1. 数据库迁移

```bash
# 执行数据库迁移脚本
cd api
./run_wallet_migration.sh
```

### 2. 编译项目

```bash
cd api
mvn clean package
```

### 3. 启动服务

```bash
cd api
./run.sh
```

### 4. 测试 API

```bash
cd api
./test_wallet_api.sh -t your_jwt_token --all
```

## 配置说明

### 钱包配置参数

- `serviceFeeRate`: 技术服务费率（默认 0.2，即 20%）
- `withdrawalDelayDays`: 提现延迟天数（默认 7 天）
- `minWithdrawalAmount`: 最小提现金额（默认 10.00 元）
- `maxWithdrawalAmount`: 最大提现金额（默认 50000.00 元）
- `dailyWithdrawalLimit`: 每日提现限额（默认 100000.00 元）
- `withdrawalFeeRate`: 提现手续费率（默认 0.02，即 2%）
- `minWithdrawalFee`: 最小提现手续费（默认 1.00 元）
- `maxWithdrawalFee`: 最大提现手续费（默认 50.00 元）

### 定时任务配置

- 每天凌晨 2 点处理延迟到账
- 每小时检查一次延迟到账（可选）

## 业务规则

### 提现限制

- 最小提现金额：10.00 元
- 最大提现金额：50,000.00 元
- 每日提现限额：100,000.00 元

### 延迟到账

- 订单完成后 7 天才能提现
- 技术服务费为订单金额的 20%

### 手续费

- 提现手续费为提现金额的 2%
- 最低手续费 1.00 元，最高手续费 50.00 元

## 错误码说明

| 错误码 | 说明             |
| ------ | ---------------- |
| 0      | 成功             |
| 1001   | 参数错误         |
| 1004   | 资源不存在       |
| 1005   | 操作失败         |
| 2001   | 余额不足         |
| 2002   | 可提现金额不足   |
| 2003   | 提现金额超出限制 |
| 2004   | 提现频率超限     |

## 安全考虑

### 数据安全

- 所有金额计算使用 BigDecimal，避免浮点数精度问题
- 所有钱包操作使用事务，确保数据一致性
- 关键操作添加并发控制，防止重复操作

### 业务安全

- 提现金额限制（最小提现金额、最大提现金额）
- 提现频率限制（每日提现次数限制）
- 异常操作监控和告警

### 接口安全

- 所有钱包接口需要用户认证
- 敏感操作需要二次验证
- 接口调用频率限制

## 监控和告警

### 关键指标监控

- 钱包余额变化
- 提现申请数量
- 延迟到账处理状态
- 异常交易数量

### 告警规则

- 大额提现申请（超过设定阈值）
- 异常余额变动
- 延迟到账处理失败
- 系统错误率过高

## 测试用例

### 功能测试

- 用户充值测试
- 订单收入分配测试
- 延迟到账测试
- 提现申请测试
- 余额计算测试

### 异常测试

- 余额不足测试
- 并发操作测试
- 网络异常测试
- 数据一致性测试

## 常见问题

### Q1: 如何处理并发提现？

A1: 使用数据库事务和乐观锁机制，确保同一时间只能处理一个提现申请。

### Q2: 延迟到账失败怎么办？

A2: 定时任务会重试处理，同时记录详细的错误日志，便于排查问题。

### Q3: 如何保证金额计算的准确性？

A3: 使用 BigDecimal 进行所有金额计算，避免浮点数精度问题。

### Q4: 提现申请状态如何管理？

A4: 提现申请有完整的状态流转：pending → processing → success/failed/cancelled。

## 联系信息

如有问题或建议，请联系：

- **技术支持**: tech@aiphone.com
- **产品反馈**: product@aiphone.com
- **文档更新**: docs@aiphone.com
