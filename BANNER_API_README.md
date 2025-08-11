# 横幅 API 使用说明

## 概述

本文档描述了横幅相关的 API 接口，包括首页横幅、分类横幅、促销横幅等功能。

## 接口列表

### 1. 获取首页横幅列表

**接口地址**: `GET /api/banners/home`

**功能描述**: 获取首页横幅列表，支持数量限制

**请求参数**:

- `limit` (可选): 数量限制，默认 5，最大 20

**请求示例**:

```bash
# 获取默认5个首页横幅
GET /api/banners/home

# 获取10个首页横幅
GET /api/banners/home?limit=10
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
    }
  ],
  "timestamp": 1640995200000
}
```

### 2. 根据类型获取横幅列表

**接口地址**: `GET /api/banners/type/{type}`

**功能描述**: 根据横幅类型获取横幅列表

**路径参数**:

- `type`: 横幅类型（home-首页，category-分类页，promotion-促销页）

**请求示例**:

```bash
# 获取分类页横幅
GET /api/banners/type/category

# 获取促销页横幅
GET /api/banners/type/promotion
```

### 3. 获取所有有效的横幅列表

**接口地址**: `GET /api/banners/active`

**功能描述**: 获取所有有效的横幅列表（在有效期内且状态为启用）

**请求示例**:

```bash
GET /api/banners/active
```

### 4. 获取横幅列表（分页）

**接口地址**: `GET /api/banners`

**功能描述**: 获取横幅列表，支持分页、类型筛选、状态筛选

**请求参数**:

- `page` (可选): 页码，默认 1
- `pageSize` (可选): 每页数量，默认 10，最大 50
- `type` (可选): 横幅类型筛选
- `status` (可选): 状态筛选：1-启用，0-禁用

**请求示例**:

```bash
# 获取第一页横幅列表
GET /api/banners

# 获取首页横幅列表（启用状态）
GET /api/banners?page=1&pageSize=10&type=home&status=1
```

### 5. 获取横幅详情

**接口地址**: `GET /api/banners/{id}`

**功能描述**: 根据横幅 ID 获取横幅详细信息

**路径参数**:

- `id`: 横幅 ID

**请求示例**:

```bash
GET /api/banners/1
```

### 6. 创建横幅

**接口地址**: `POST /api/banners`

**功能描述**: 创建新的横幅

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

**请求示例**:

```bash
curl -X POST "http://localhost:8080/api/banners" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "新横幅",
    "description": "横幅描述",
    "imageUrl": "https://example.com/banner.jpg",
    "linkUrl": "https://example.com/link",
    "type": "home",
    "sortWeight": 100,
    "status": 1
  }'
```

### 7. 更新横幅信息

**接口地址**: `PUT /api/banners/{id}`

**功能描述**: 更新指定横幅的信息

**路径参数**:

- `id`: 横幅 ID

**请求体**: 同创建接口

**请求示例**:

```bash
curl -X PUT "http://localhost:8080/api/banners/1" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "更新后的横幅",
    "description": "更新后的描述",
    "imageUrl": "https://example.com/updated-banner.jpg",
    "linkUrl": "https://example.com/updated-link",
    "type": "home",
    "sortWeight": 200,
    "status": 1
  }'
```

### 8. 删除横幅

**接口地址**: `DELETE /api/banners/{id}`

**功能描述**: 删除指定横幅

**路径参数**:

- `id`: 横幅 ID

**请求示例**:

```bash
curl -X DELETE "http://localhost:8080/api/banners/1"
```

### 9. 启用横幅

**接口地址**: `PUT /api/banners/{id}/enable`

**功能描述**: 启用指定横幅

**路径参数**:

- `id`: 横幅 ID

**请求示例**:

```bash
curl -X PUT "http://localhost:8080/api/banners/1/enable"
```

### 10. 禁用横幅

**接口地址**: `PUT /api/banners/{id}/disable`

**功能描述**: 禁用指定横幅

**路径参数**:

- `id`: 横幅 ID

**请求示例**:

```bash
curl -X PUT "http://localhost:8080/api/banners/1/disable"
```

## 数据模型

### BannerDTO

```json
{
  "id": 1,
  "title": "横幅标题",
  "description": "横幅描述",
  "imageUrl": "图片URL",
  "linkUrl": "跳转链接",
  "type": "横幅类型",
  "sortWeight": 100,
  "status": 1,
  "startTime": "2024-01-01T00:00:00",
  "endTime": "2024-02-01T00:00:00",
  "createdAt": "2024-01-01T00:00:00",
  "updatedAt": "2024-01-01T00:00:00"
}
```

## 字段说明

| 字段名      | 类型          | 说明                                                   |
| ----------- | ------------- | ------------------------------------------------------ |
| id          | Long          | 横幅 ID                                                |
| title       | String        | 横幅标题                                               |
| description | String        | 横幅描述                                               |
| imageUrl    | String        | 横幅图片 URL                                           |
| linkUrl     | String        | 跳转链接                                               |
| type        | String        | 横幅类型：home-首页，category-分类页，promotion-促销页 |
| sortWeight  | Integer       | 排序权重，数字越大越靠前                               |
| status      | Integer       | 状态：1-启用，0-禁用                                   |
| startTime   | LocalDateTime | 开始时间（可选）                                       |
| endTime     | LocalDateTime | 结束时间（可选）                                       |
| createdAt   | LocalDateTime | 创建时间                                               |
| updatedAt   | LocalDateTime | 更新时间                                               |

## 错误码说明

| 错误码 | 说明       |
| ------ | ---------- |
| 0      | 成功       |
| 1001   | 参数错误   |
| 1004   | 资源不存在 |
| 1005   | 服务器错误 |

## 使用建议

1. **首页横幅**: 建议在首页使用，展示重要活动和推广信息
2. **分类横幅**: 在分类页面使用，展示特定分类的推广内容
3. **促销横幅**: 在促销页面使用，展示促销活动信息
4. **时间控制**: 利用 startTime 和 endTime 控制横幅的显示时间
5. **排序权重**: 通过 sortWeight 控制横幅的显示顺序

## 注意事项

1. 所有获取横幅的接口都会自动过滤掉已过期的横幅
2. 横幅的状态控制可以快速启用/禁用横幅
3. 图片 URL 需要确保可访问性
4. 跳转链接可以是内部页面路径或外部链接
5. 时间字段使用 ISO 8601 格式

## 测试

项目包含完整的单元测试，可以通过以下命令运行测试：

```bash
mvn test
```

测试文件位置：`src/test/java/com/aiphone/BannerControllerTest.java`

## 测试脚本

项目提供了测试脚本，可以通过以下命令运行：

```bash
./test_banner_api.sh
```

测试脚本会测试所有主要的横幅接口。
