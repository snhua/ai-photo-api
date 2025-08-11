# AI 绘画师小程序后台 API 文档

## 概述

本文档描述了 AI 绘画师小程序的后台 API 接口，包括用户管理、AI 绘画师管理、订单管理等功能。

## 基础信息

- **基础 URL**: `http://api.aiphoto.com:8080`
- **API 版本**: v1.0
- **数据格式**: JSON
- **字符编码**: UTF-8

## 通用响应格式

```json
{
  "code": 0,
  "message": "success",
  "data": {},
  "timestamp": 1640995200000
}
```

## AI 绘画师相关接口

### 1. 获取推荐 AI 绘画师列表

**接口地址**: `GET /api/artists/recommended`

**请求参数**:

- `page` (可选): 页码，默认 1
- `pageSize` (可选): 每页数量，默认 10
- `category` (可选): 分类筛选，如"风景画"、"人物画"等
- `sort` (可选): 排序方式，可选值：rating(评分)、orders(订单数)、price(价格)

**请求示例**:

```
GET /api/artists/recommended?page=1&pageSize=5&category=风景画&sort=rating
```

**响应示例**:

```json
{
  "code": 0,
  "message": "success",
  "data": {
    "records": [
      {
        "id": 1,
        "userId": 2,
        "artistName": "AI绘画师小明",
        "description": "专业AI绘画师，擅长风景和人物绘画",
        "specialties": ["风景画", "人物画", "写实风格"],
        "pricePerHour": 100.0,
        "rating": 4.8,
        "totalOrders": 156,
        "status": 1,
        "createdAt": "2024-01-01T00:00:00",
        "user": {
          "id": 2,
          "nickname": "AI绘画师小明",
          "avatar": "https://example.com/avatar1.jpg",
          "userType": "artist"
        }
      }
    ],
    "total": 5,
    "size": 10,
    "current": 1,
    "pages": 1
  }
}
```

### 2. 获取 AI 绘画师列表

**接口地址**: `GET /api/artists`

**请求参数**:

- `page` (可选): 页码，默认 1
- `pageSize` (可选): 每页数量，默认 10
- `category` (可选): 分类筛选
- `sort` (可选): 排序方式
- `keyword` (可选): 搜索关键词

**请求示例**:

```
GET /api/artists?page=1&pageSize=10&keyword=风景画&sort=rating
```

### 3. 获取 AI 绘画师详情

**接口地址**: `GET /api/artists/{id}`

**路径参数**:

- `id`: 绘画师 ID

**请求示例**:

```
GET /api/artists/1
```

**响应示例**:

```json
{
  "code": 0,
  "message": "success",
  "data": {
    "id": 1,
    "userId": 2,
    "artistName": "AI绘画师小明",
    "description": "专业AI绘画师，擅长风景和人物绘画",
    "specialties": ["风景画", "人物画", "写实风格"],
    "pricePerHour": 100.0,
    "rating": 4.8,
    "totalOrders": 156,
    "status": 1,
    "createdAt": "2024-01-01T00:00:00",
    "user": {
      "id": 2,
      "nickname": "AI绘画师小明",
      "avatar": "https://example.com/avatar1.jpg",
      "userType": "artist"
    },
    "works": [
      {
        "id": 1,
        "title": "梦幻森林",
        "description": "美丽的森林风景画",
        "imageUrl": "https://example.com/artwork1.jpg",
        "category": "风景画",
        "tags": ["森林", "梦幻", "自然"],
        "price": 299.0
      }
    ],
    "reviews": [
      {
        "id": 1,
        "rating": 5,
        "content": "作品质量很高，很满意！",
        "tags": ["专业", "速度快"],
        "createdAt": "2024-01-01T00:00:00",
        "user": {
          "nickname": "用户小明",
          "avatar": "https://example.com/user1.jpg"
        }
      }
    ]
  }
}
```

### 4. 根据用户 ID 获取 AI 绘画师信息

**接口地址**: `GET /api/artists/user/{userId}`

**路径参数**:

- `userId`: 用户 ID

**请求示例**:

```
GET /api/artists/user/2
```

### 5. 获取绘画师作品列表（分页）

**接口地址**: `GET /api/artists/{id}/works`

**路径参数**:

- `id`: 绘画师 ID

**请求参数**:

- `page` (可选): 页码，默认 1
- `pageSize` (可选): 每页数量，默认 10
- `category` (可选): 分类筛选
- `sort` (可选): 排序方式，可选值：latest(最新)、price(价格)

**请求示例**:

```
GET /api/artists/2/works?page=1&pageSize=5&sort=latest
```

**响应示例**:

```json
{
  "code": 0,
  "message": "success",
  "data": {
    "records": [
      {
        "id": 1,
        "artistId": 2,
        "title": "梦幻森林",
        "description": "美丽的森林风景画，充满梦幻色彩",
        "imageUrl": "https://example.com/artwork1.jpg",
        "category": "风景画",
        "tags": ["森林", "梦幻", "自然", "风景"],
        "price": 299.0,
        "status": 1,
        "createdAt": "2024-01-01T00:00:00"
      },
      {
        "id": 2,
        "artistId": 2,
        "title": "山水画",
        "description": "传统山水画风格，意境深远",
        "imageUrl": "https://example.com/artwork2.jpg",
        "category": "风景画",
        "tags": ["山水", "传统", "意境"],
        "price": 399.0,
        "status": 1,
        "createdAt": "2024-01-02T00:00:00"
      }
    ],
    "total": 2,
    "size": 10,
    "current": 1,
    "pages": 1
  }
}
```

### 6. 获取绘画师作品列表（不分页）

**接口地址**: `GET /api/artists/{id}/works/list`

**路径参数**:

- `id`: 绘画师 ID

**请求参数**:

- `limit` (可选): 数量限制，默认 10

**请求示例**:

```
GET /api/artists/2/works/list?limit=5
```

**响应示例**:

```json
{
  "code": 0,
  "message": "success",
  "data": [
    {
      "id": 1,
      "artistId": 2,
      "title": "梦幻森林",
      "description": "美丽的森林风景画，充满梦幻色彩",
      "imageUrl": "https://example.com/artwork1.jpg",
      "category": "风景画",
      "tags": ["森林", "梦幻", "自然", "风景"],
      "price": 299.0,
      "status": 1,
      "createdAt": "2024-01-01T00:00:00"
    },
    {
      "id": 2,
      "artistId": 2,
      "title": "山水画",
      "description": "传统山水画风格，意境深远",
      "imageUrl": "https://example.com/artwork2.jpg",
      "category": "风景画",
      "tags": ["山水", "传统", "意境"],
      "price": 399.0,
      "status": 1,
      "createdAt": "2024-01-02T00:00:00"
    }
  ]
}
```

### 7. 创建 AI 绘画师

**接口地址**: `POST /api/artists`

**请求体**:

```json
{
  "userId": 2,
  "artistName": "AI绘画师小明",
  "description": "专业AI绘画师，擅长风景和人物绘画",
  "specialties": ["风景画", "人物画", "写实风格"],
  "pricePerHour": 100.0
}
```

### 8. 更新 AI 绘画师信息

**接口地址**: `PUT /api/artists/{id}`

**路径参数**:

- `id`: 绘画师 ID

**请求体**: 同创建接口

### 9. 删除 AI 绘画师

**接口地址**: `DELETE /api/artists/{id}`

**路径参数**:

- `id`: 绘画师 ID

### 10. 更新绘画师评分

**接口地址**: `PUT /api/artists/{id}/rating`

**路径参数**:

- `id`: 绘画师 ID

**请求参数**:

- `rating`: 新评分（0-5）

**请求示例**:

```
PUT /api/artists/1/rating?rating=4.9
```

### 11. 更新绘画师订单数

**接口地址**: `PUT /api/artists/{id}/orders`

**路径参数**:

- `id`: 绘画师 ID

**请求参数**:

- `totalOrders`: 总订单数

**请求示例**:

```
PUT /api/artists/1/orders?totalOrders=200
```

## 作品相关接口

### 1. 获取热门作品列表

**接口地址**: `GET /api/artworks/hot`

**请求参数**:

- `limit` (可选): 数量限制，默认 10
- `category` (可选): 分类筛选，如"风景画"、"人物画"等

**请求示例**:

```
GET /api/artworks/hot?limit=5&category=风景画
```

**响应示例**:

```json
{
  "code": 0,
  "message": "success",
  "data": [
    {
      "id": 1,
      "artistId": 2,
      "title": "梦幻森林",
      "description": "美丽的森林风景画，充满梦幻色彩",
      "imageUrl": "https://example.com/artwork1.jpg",
      "category": "风景画",
      "tags": ["森林", "梦幻", "自然", "风景"],
      "price": 299.0,
      "status": 1,
      "createdAt": "2024-01-01T00:00:00"
    },
    {
      "id": 2,
      "artistId": 3,
      "title": "山水画",
      "description": "传统山水画风格，意境深远",
      "imageUrl": "https://example.com/artwork2.jpg",
      "category": "风景画",
      "tags": ["山水", "传统", "意境"],
      "price": 399.0,
      "status": 1,
      "createdAt": "2024-01-02T00:00:00"
    }
  ]
}
```

### 2. 获取作品列表

**接口地址**: `GET /api/artworks`

**请求参数**:

- `page` (可选): 页码，默认 1
- `pageSize` (可选): 每页数量，默认 10
- `category` (可选): 分类筛选
- `sort` (可选): 排序方式，可选值：latest(最新)、price(价格)、popular(热门)
- `keyword` (可选): 搜索关键词

**请求示例**:

```
GET /api/artworks?page=1&pageSize=10&category=风景画&sort=latest
```

### 3. 获取作品详情

**接口地址**: `GET /api/artworks/{id}`

**路径参数**:

- `id`: 作品 ID

**请求示例**:

```
GET /api/artworks/1
```

**响应示例**:

```json
{
  "code": 0,
  "message": "success",
  "data": {
    "id": 1,
    "artistId": 2,
    "title": "梦幻森林",
    "description": "美丽的森林风景画，充满梦幻色彩",
    "imageUrl": "https://example.com/artwork1.jpg",
    "category": "风景画",
    "tags": ["森林", "梦幻", "自然", "风景"],
    "price": 299.0,
    "status": 1,
    "createdAt": "2024-01-01T00:00:00"
  }
}
```

### 4. 根据绘画师 ID 获取作品列表

**接口地址**: `GET /api/artworks/artist/{artistId}`

**路径参数**:

- `artistId`: 绘画师 ID

**请求示例**:

```
GET /api/artworks/artist/2
```

### 5. 根据分类获取作品列表

**接口地址**: `GET /api/artworks/category/{category}`

**路径参数**:

- `category`: 作品分类

**请求示例**:

```
GET /api/artworks/category/风景画
```

### 6. 搜索作品

**接口地址**: `GET /api/artworks/search`

**请求参数**:

- `keyword`: 搜索关键词

**请求示例**:

```
GET /api/artworks/search?keyword=森林
```

## 横幅相关接口

### 1. 获取首页横幅列表

**接口地址**: `GET /api/banners/home`

**请求参数**:

- `limit` (可选): 数量限制，默认 5

**请求示例**:

```
GET /api/banners/home?limit=5
```

**响应示例**:

```json
{
  "code": 0,
  "message": "success",
  "data": [
    {
      "id": 1,
      "title": "首页横幅1",
      "description": "首页横幅描述1",
      "imageUrl": "https://example.com/banner1.jpg",
      "linkUrl": "https://example.com/link1",
      "type": "home",
      "sortWeight": 100,
      "status": 1,
      "startTime": "2024-01-01T00:00:00",
      "endTime": "2024-02-01T00:00:00",
      "createdAt": "2024-01-01T00:00:00",
      "updatedAt": "2024-01-01T00:00:00"
    },
    {
      "id": 2,
      "title": "首页横幅2",
      "description": "首页横幅描述2",
      "imageUrl": "https://example.com/banner2.jpg",
      "linkUrl": "https://example.com/link2",
      "type": "home",
      "sortWeight": 90,
      "status": 1,
      "startTime": "2024-01-01T00:00:00",
      "endTime": "2024-02-01T00:00:00",
      "createdAt": "2024-01-01T00:00:00",
      "updatedAt": "2024-01-01T00:00:00"
    }
  ]
}
```

### 2. 根据类型获取横幅列表

**接口地址**: `GET /api/banners/type/{type}`

**路径参数**:

- `type`: 横幅类型（home-首页，category-分类页，promotion-促销页）

**请求示例**:

```
GET /api/banners/type/category
```

### 3. 获取所有有效的横幅列表

**接口地址**: `GET /api/banners/active`

**请求示例**:

```
GET /api/banners/active
```

### 4. 获取横幅列表（分页）

**接口地址**: `GET /api/banners`

**请求参数**:

- `page` (可选): 页码，默认 1
- `pageSize` (可选): 每页数量，默认 10
- `type` (可选): 横幅类型
- `status` (可选): 状态：1-启用，0-禁用

**请求示例**:

```
GET /api/banners?page=1&pageSize=10&type=home&status=1
```

### 5. 获取横幅详情

**接口地址**: `GET /api/banners/{id}`

**路径参数**:

- `id`: 横幅 ID

**请求示例**:

```
GET /api/banners/1
```

**响应示例**:

```json
{
  "code": 0,
  "message": "success",
  "data": {
    "id": 1,
    "title": "首页横幅1",
    "description": "首页横幅描述1",
    "imageUrl": "https://example.com/banner1.jpg",
    "linkUrl": "https://example.com/link1",
    "type": "home",
    "sortWeight": 100,
    "status": 1,
    "startTime": "2024-01-01T00:00:00",
    "endTime": "2024-02-01T00:00:00",
    "createdAt": "2024-01-01T00:00:00",
    "updatedAt": "2024-01-01T00:00:00"
  }
}
```

### 6. 创建横幅

**接口地址**: `POST /api/banners`

**请求体**:

```json
{
  "title": "新横幅",
  "description": "横幅描述",
  "imageUrl": "https://example.com/banner.jpg",
  "linkUrl": "https://example.com/link",
  "type": "home",
  "sortWeight": 100,
  "status": 1,
  "startTime": "2024-01-01T00:00:00",
  "endTime": "2024-02-01T00:00:00"
}
```

### 7. 更新横幅信息

**接口地址**: `PUT /api/banners/{id}`

**路径参数**:

- `id`: 横幅 ID

**请求体**: 同创建接口

### 8. 删除横幅

**接口地址**: `DELETE /api/banners/{id}`

**路径参数**:

- `id`: 横幅 ID

### 9. 启用横幅

**接口地址**: `PUT /api/banners/{id}/enable`

**路径参数**:

- `id`: 横幅 ID

### 10. 禁用横幅

**接口地址**: `PUT /api/banners/{id}/disable`

**路径参数**:

- `id`: 横幅 ID

## 错误码说明

| 错误码 | 说明       |
| ------ | ---------- |
| 0      | 成功       |
| 1001   | 参数错误   |
| 1002   | 未授权     |
| 1003   | 禁止访问   |
| 1004   | 资源不存在 |
| 1005   | 服务器错误 |

## 数据库表结构

### artists 表

```sql
CREATE TABLE `artists` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '绘画师ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `artist_name` VARCHAR(100) COMMENT '绘画师名称',
    `description` TEXT COMMENT '绘画师描述',
    `specialties` TEXT COMMENT '专长领域，JSON格式',
    `price_per_hour` DECIMAL(10,2) COMMENT '每小时价格',
    `rating` DECIMAL(3,2) DEFAULT 0.00 COMMENT '评分',
    `total_orders` INT DEFAULT 0 COMMENT '总订单数',
    `status` TINYINT DEFAULT 1 COMMENT '状态：1-正常，0-禁用',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
);
```

### banners 表

```sql
CREATE TABLE `banners` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '横幅ID',
    `title` VARCHAR(200) COMMENT '横幅标题',
    `description` TEXT COMMENT '横幅描述',
    `image_url` VARCHAR(255) COMMENT '横幅图片URL',
    `link_url` VARCHAR(255) COMMENT '跳转链接',
    `type` VARCHAR(50) COMMENT '横幅类型：home-首页，category-分类页，promotion-促销页',
    `sort_weight` INT DEFAULT 0 COMMENT '排序权重，数字越大越靠前',
    `status` TINYINT DEFAULT 1 COMMENT '状态：1-启用，0-禁用',
    `start_time` TIMESTAMP NULL COMMENT '开始时间',
    `end_time` TIMESTAMP NULL COMMENT '结束时间',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX `idx_type_status` (`type`, `status`),
    INDEX `idx_sort_weight` (`sort_weight`),
    INDEX `idx_start_time` (`start_time`),
    INDEX `idx_end_time` (`end_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='横幅表';
```

## 部署说明

1. 确保 MySQL 数据库已创建并运行
2. 执行 `database.sql` 创建数据库表结构
3. 执行 `artist_test_data.sql` 插入测试数据
4. 配置 `application.yml` 中的数据库连接信息
5. 启动 Spring Boot 应用

## 测试数据

项目包含完整的测试数据，包括：

- 5 个 AI 绘画师用户
- 10 个作品示例
- 5 个评价示例
- 3 个横幅示例

可以通过执行 `artist_test_data.sql` 脚本来插入测试数据。
