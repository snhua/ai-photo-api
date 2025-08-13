# 画师工作台功能说明

## 概述

画师工作台是专门为 AI 绘画师设计的核心功能模块，提供完整的订单管理、作品上传、工作流程等功能。

## 主要功能

### 1. 订单管理

#### 1.1 获取可接订单列表

- **接口**: `GET /artist/workbench/available-orders`
- **功能**: 获取当前可接的订单列表
- **参数**:
  - `page`: 页码（默认 1）
  - `pageSize`: 每页数量（默认 10）
  - `category`: 分类筛选
  - `priceRange`: 价格范围（格式：min-max）

#### 1.2 接单

- **接口**: `POST /artist/workbench/orders/{orderId}/accept`
- **功能**: 接受指定订单
- **权限**: 需要画师身份验证

#### 1.3 获取我的订单列表

- **接口**: `GET /artist/workbench/my-orders`
- **功能**: 获取当前画师的所有订单
- **参数**:
  - `page`: 页码
  - `pageSize`: 每页数量
  - `status`: 订单状态筛选

#### 1.4 开始制作订单

- **接口**: `POST /artist/workbench/orders/{orderId}/start`
- **功能**: 开始制作指定订单
- **权限**: 需要是订单的画师

#### 1.5 获取订单详情

- **接口**: `GET /artist/workbench/orders/{orderId}`
- **功能**: 获取订单详细信息
- **权限**: 需要是订单的画师

### 2. 作品管理

#### 2.1 上传作品草稿

- **接口**: `POST /artist/workbench/orders/{orderId}/draft`
- **功能**: 为订单上传作品草稿
- **参数**:
  - `file`: 作品文件
  - `description`: 草稿说明
- **权限**: 需要是订单的画师

#### 2.2 提交最终作品

- **接口**: `POST /artist/workbench/orders/{orderId}/deliver`
- **功能**: 提交最终完成的作品
- **参数**: `DeliveryDTO` 对象
- **权限**: 需要是订单的画师

#### 2.3 获取我的作品列表

- **接口**: `GET /artist/workbench/my-works`
- **功能**: 获取画师的作品集
- **参数**:
  - `page`: 页码
  - `pageSize`: 每页数量
  - `category`: 分类筛选

#### 2.4 上传作品到作品集

- **接口**: `POST /artist/workbench/works`
- **功能**: 上传作品到个人作品集
- **参数**:
  - `file`: 作品文件
  - `artworkDTO`: 作品信息

#### 2.5 更新作品信息

- **接口**: `PUT /artist/workbench/works/{workId}`
- **功能**: 更新作品信息
- **权限**: 需要是作品的画师

#### 2.6 删除作品

- **接口**: `DELETE /artist/workbench/works/{workId}`
- **功能**: 删除作品
- **权限**: 需要是作品的画师

### 3. 工作统计

#### 3.1 获取工作统计信息

- **接口**: `GET /artist/workbench/statistics`
- **功能**: 获取画师的工作统计数据
- **返回数据**:
  - 订单统计（总数、各状态数量）
  - 作品总数
  - 总收入

## 数据模型

### DeliveryDTO（作品交付信息）

```json
{
  "artworkUrls": ["作品文件URL列表"],
  "title": "作品标题",
  "description": "作品描述",
  "notes": "作品说明",
  "includeSourceFiles": true,
  "sourceFileUrls": ["源文件URL列表"],
  "tags": ["作品标签"],
  "category": "作品分类",
  "style": "作品风格",
  "dimensions": "作品尺寸",
  "format": "作品格式",
  "resolution": "作品分辨率",
  "workHours": 8,
  "technicalNotes": "技术说明",
  "feedbackHandling": "客户反馈处理说明"
}
```

## 工作流程

### 1. 接单流程

1. 画师浏览可接订单列表
2. 选择合适的订单
3. 点击接单
4. 系统验证订单状态
5. 更新订单为已接单状态

### 2. 制作流程

1. 画师开始制作订单
2. 上传草稿（可选）
3. 制作完成
4. 提交最终作品
5. 订单状态更新为已完成

### 3. 作品管理流程

1. 画师上传作品到作品集
2. 管理作品信息
3. 展示给客户浏览

## 权限控制

### 身份验证

- 所有接口都需要 JWT token 验证
- 通过`SecurityUtils.getCurrentUserId()`获取当前用户 ID

### 权限验证

- 订单相关操作需要验证画师是否为订单的画师
- 作品相关操作需要验证画师是否为作品的画师

## 错误处理

### 常见错误码

- `1003`: 无权限
- `1004`: 资源不存在或不属于当前用户
- `1005`: 系统错误

### 错误响应格式

```json
{
  "code": 1004,
  "message": "订单不存在或不属于您",
  "data": null
}
```

## 测试

### 测试脚本

使用 `test_artist_workbench.sh` 脚本进行功能测试：

```bash
./test_artist_workbench.sh
```

### 测试前准备

1. 确保数据库中有订单数据
2. 确保 COS 服务配置正确
3. 确保用户已注册为画师角色
4. 启动应用服务

## 配置要求

### 数据库

- 需要 orders 表
- 需要 artworks 表
- 需要 users 表
- 需要 artists 表

### 文件存储

- 配置腾讯云 COS 服务
- 设置正确的存储桶和区域

### 安全配置

- 配置 JWT 密钥
- 设置正确的 CORS 策略

## 注意事项

1. **文件上传限制**: 注意设置合理的文件大小限制
2. **并发控制**: 接单时需要防止并发问题
3. **数据一致性**: 订单状态变更需要保证数据一致性
4. **性能优化**: 大量数据查询时需要考虑分页和索引
5. **安全考虑**: 文件上传需要验证文件类型和大小

## 扩展功能

### 可扩展的功能

1. 订单评价系统
2. 画师等级系统
3. 作品版权管理
4. 收入统计报表
5. 客户沟通系统
6. 作品版本管理

### 技术优化

1. 文件压缩和优化
2. 缓存机制
3. 异步处理
4. 消息队列
5. 监控和日志
