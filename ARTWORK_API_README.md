# 作品 API 使用说明

## 概述

本文档描述了作品相关的 API 接口，包括热门作品、作品列表、作品详情等功能。

## 接口列表

### 1. 获取热门作品列表

**接口地址**: `GET /api/artworks/hot`

**功能描述**: 获取热门作品列表，支持按分类筛选

**请求参数**:

- `limit` (可选): 数量限制，默认 10，最大 50
- `category` (可选): 分类筛选，如"风景画"、"人物画"等

**请求示例**:

```bash
# 获取默认10个热门作品
GET /api/artworks/hot

# 获取5个风景画热门作品
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
    }
  ],
  "timestamp": 1640995200000
}
```

### 2. 获取作品列表

**接口地址**: `GET /api/artworks`

**功能描述**: 获取作品列表，支持分页、分类筛选、排序和搜索

**请求参数**:

- `page` (可选): 页码，默认 1
- `pageSize` (可选): 每页数量，默认 10，最大 50
- `category` (可选): 分类筛选
- `sort` (可选): 排序方式，可选值：latest(最新)、price(价格)、popular(热门)
- `keyword` (可选): 搜索关键词

**请求示例**:

```bash
# 获取第一页作品列表
GET /api/artworks

# 获取风景画分类的最新作品
GET /api/artworks?page=1&pageSize=10&category=风景画&sort=latest

# 搜索包含"森林"的作品
GET /api/artworks?keyword=森林&sort=latest
```

### 3. 获取作品详情

**接口地址**: `GET /api/artworks/{id}`

**功能描述**: 根据作品 ID 获取作品详细信息

**路径参数**:

- `id`: 作品 ID

**请求示例**:

```bash
GET /api/artworks/1
```

### 4. 根据绘画师 ID 获取作品列表

**接口地址**: `GET /api/artworks/artist/{artistId}`

**功能描述**: 获取指定绘画师的所有作品

**路径参数**:

- `artistId`: 绘画师 ID

**请求示例**:

```bash
GET /api/artworks/artist/2
```

### 5. 根据分类获取作品列表

**接口地址**: `GET /api/artworks/category/{category}`

**功能描述**: 获取指定分类的所有作品

**路径参数**:

- `category`: 作品分类

**请求示例**:

```bash
GET /api/artworks/category/风景画
```

### 6. 搜索作品

**接口地址**: `GET /api/artworks/search`

**功能描述**: 根据关键词搜索作品

**请求参数**:

- `keyword`: 搜索关键词

**请求示例**:

```bash
GET /api/artworks/search?keyword=森林
```

## 数据模型

### ArtworkDTO

```json
{
  "id": 1,
  "artistId": 2,
  "title": "作品标题",
  "description": "作品描述",
  "imageUrl": "图片URL",
  "category": "作品分类",
  "tags": ["标签1", "标签2"],
  "price": 299.0,
  "status": 1,
  "createdAt": "2024-01-01T00:00:00"
}
```

## 错误码说明

| 错误码 | 说明       |
| ------ | ---------- |
| 0      | 成功       |
| 1001   | 参数错误   |
| 1004   | 资源不存在 |
| 1005   | 服务器错误 |

## 使用建议

1. **热门作品接口**: 建议在首页使用，展示最新、最受欢迎的作品
2. **分类筛选**: 可以根据用户兴趣展示特定分类的作品
3. **搜索功能**: 支持标题、描述、标签的模糊搜索
4. **分页加载**: 大量数据建议使用分页加载，提升用户体验

## 注意事项

1. 所有接口返回的作品都是状态为正常(status=1)的作品
2. 热门作品的排序规则是按创建时间倒序排列
3. 搜索功能支持中文关键词
4. 图片 URL 需要确保可访问性
5. 价格字段为 BigDecimal 类型，精确到小数点后 2 位

## 测试

项目包含完整的单元测试，可以通过以下命令运行测试：

```bash
mvn test
```

测试文件位置：`src/test/java/com/aiphone/ArtworkControllerTest.java`
