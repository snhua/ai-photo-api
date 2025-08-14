# 订单状态更新说明

## 概述

本次更新为订单表添加了新的状态值 `paid` 和 `confirmed`：

- `paid`：表示用户付款完成，画师可以接单
- `confirmed`：表示用户已确认收货

## 更新内容

### 1. 数据库更新

- 在 `orders` 表的 `status` 字段中添加 `paid` 和 `confirmed` 状态值
- 新的状态枚举：`('pending', 'paid', 'accepted', 'in_progress', 'completed', 'confirmed', 'cancelled')`

### 2. 后端代码更新

- 更新了 `Order` 实体类的注释
- 修改了 `OrderServiceImpl.confirmOrder()` 方法，现在会将订单状态更新为 `confirmed`
- 更新了 `OrderStatistics` 类，添加了 `paid` 和 `confirmed` 状态的统计
- 更新了订单统计方法，包含 `paid` 和 `confirmed` 状态的统计
- 更新了 `getAvailableOrders()` 方法，现在查询 `paid` 状态的订单供画师接单

### 3. 前端代码更新

- 更新了订单详情页面的状态显示
- 更新了订单列表页面的状态显示
- 更新了订单状态标签组件
- 添加了 `paid` 和 `confirmed` 状态的样式和图标
- 更新了订单进度步骤，添加了画师接单步骤
- 更新了操作按钮的显示逻辑

## 执行步骤

### 1. 备份数据库

```bash
mysqldump -u root -p aiphone > aiphone_backup_$(date +%Y%m%d_%H%M%S).sql
```

### 2. 执行数据库更新

```bash
# 方法1：使用脚本（推荐）
./api/update_order_status.sh

# 方法2：手动执行SQL
mysql -u root -p aiphone < api/order_status_update.sql
```

### 3. 重启后端服务

```bash
cd api
./stop.sh
./run.sh
```

### 4. 重新编译前端

```bash
cd aiphone-app
npm run build
```

## 状态说明

| 状态值      | 中文名称   | 说明                     |
| ----------- | ---------- | ------------------------ |
| pending     | 待支付     | 订单已创建，等待用户支付 |
| paid        | 已支付     | 用户付款完成，画师可接单 |
| accepted    | 已接单     | 画师已接受订单           |
| in_progress | 制作中     | 画师正在制作作品         |
| completed   | 已完成     | 画师已完成作品提交       |
| confirmed   | 已确认收货 | 用户已确认收货           |
| cancelled   | 已取消     | 订单已取消               |

## 注意事项

1. **备份重要**：执行数据库更新前请务必备份数据库
2. **测试验证**：更新后请测试确认收货功能是否正常工作
3. **兼容性**：现有订单的状态不会受到影响，只有新确认的订单会使用 `confirmed` 状态

## 验证方法

1. 检查数据库中的状态枚举：

```sql
SHOW COLUMNS FROM orders LIKE 'status';
```

2. 测试支付和确认收货功能：

   - 创建一个测试订单
   - 将订单状态设置为 `pending`
   - 模拟支付，将状态设置为 `paid`
   - 验证画师可以接单
   - 将订单状态设置为 `completed`
   - 点击确认收货按钮
   - 验证订单状态是否变为 `confirmed`

3. 检查前端显示：
   - 支付后，订单状态应显示为"已支付"
   - 状态图标应显示为 💰
   - 确认收货后，订单状态应显示为"已确认收货"
   - 状态图标应显示为 🎉
   - 订单进度应显示所有步骤的完成状态
