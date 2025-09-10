# Search Service 搜索服务

## 项目概述

本项目是黑马商城的搜索服务微服务，负责商品搜索功能，集成了 Elasticsearch 作为搜索引擎，通过 RabbitMQ 实现与商品服务的数据同步。

## 技术栈

- **Spring Boot 2.7.12**
- **Spring Cloud 2021.0.8**
- **Elasticsearch 7.12.1**
- **RabbitMQ**
- **Nacos** (服务注册与配置中心)
- **OpenFeign** (服务间调用)

## 核心功能

### 1. 商品搜索
- 支持关键字搜索（商品名称、品牌、分类）
- 支持品牌筛选
- 支持分类筛选
- 支持价格范围筛选
- 支持多种排序方式
- 支持分页查询

### 2. 数据同步
- 监听商品服务的 MQ 消息
- 自动同步商品新增、修改、删除操作
- 实时更新 Elasticsearch 索引

### 3. 服务集成
- 通过 Feign 调用商品服务获取商品详情
- 集成熔断降级机制

## 配置说明

### Nacos 配置（推荐）

项目使用 Nacos 作为配置中心，需要在 Nacos 中配置以下文件：

#### shared-elasticsearch.yaml
```yaml
spring:
  elasticsearch:
    uris: http://192.168.150.101:9200
    connection-timeout: 5s
    socket-timeout: 30s
```

### 本地配置（开发环境备用）

如果不使用 Nacos，可以激活 `local` profile：
```yaml
spring:
  profiles:
    active: local
```

本地配置文件 `application-local.yaml` 已包含完整的配置。

## API 接口

### 搜索商品

**接口地址：** `POST /search/list`

**请求参数：**
```json
{
  "key": "手机",           // 搜索关键字（可选）
  "brand": "华为",         // 品牌筛选（可选）
  "category": "手机",      // 分类筛选（可选）
  "minPrice": 1000,       // 最低价格（可选）
  "maxPrice": 5000,       // 最高价格（可选）
  "sortBy": "price",      // 排序字段（可选）
  "isAsc": true,          // 是否升序（可选）
  "pageNo": 1,            // 页码
  "pageSize": 10          // 页大小
}
```

**响应结果：**
```json
{
  "total": 100,
  "pages": 10,
  "list": [
    {
      "id": 1,
      "name": "华为 Mate 50",
      "price": 4999,
      "image": "http://...",
      "category": "手机",
      "brand": "华为",
      "status": 1
    }
  ]
}
```

## MQ 消息格式

### 商品新增/修改消息
- **Exchange:** `item.topic`
- **Routing Key:** `item.insert` / `item.update`
- **消息体:** 商品ID (Long)

### 商品删除消息
- **Exchange:** `item.topic`
- **Routing Key:** `item.delete`
- **消息体:** 商品ID (Long)

## 测试指南

### 1. 环境准备

确保以下服务正常运行：
- **Nacos:** http://192.168.150.101:8848
- **Elasticsearch:** http://192.168.150.101:9200
- **RabbitMQ:** 192.168.150.101:5672
- **MySQL:** 192.168.150.101:3306

### 2. 启动服务

按以下顺序启动服务：
1. 启动 `item-service`
2. 启动 `search-service`

### 3. 功能测试

#### 3.1 搜索功能测试

使用 Postman 或其他 HTTP 客户端测试搜索接口：

```bash
# 基础搜索
curl -X POST http://localhost:8084/search/list \
  -H "Content-Type: application/json" \
  -d '{
    "key": "手机",
    "pageNo": 1,
    "pageSize": 10
  }'

# 带筛选条件的搜索
curl -X POST http://localhost:8084/search/list \
  -H "Content-Type: application/json" \
  -d '{
    "key": "华为",
    "brand": "华为",
    "minPrice": 2000,
    "maxPrice": 6000,
    "sortBy": "price",
    "isAsc": true,
    "pageNo": 1,
    "pageSize": 5
  }'
```

#### 3.2 数据同步测试

1. **新增商品测试：**
   ```bash
   curl -X POST http://localhost:8081/items \
     -H "Content-Type: application/json" \
     -d '{
       "name": "测试商品",
       "price": 999,
       "category": "测试分类",
       "brand": "测试品牌",
       "status": 1
     }'
   ```
   
   观察控制台日志，确认：
   - item-service 发送 MQ 消息
   - search-service 接收并处理消息
   - Elasticsearch 索引更新成功

2. **修改商品测试：**
   ```bash
   curl -X PUT http://localhost:8081/items \
     -H "Content-Type: application/json" \
     -d '{
       "id": 1,
       "name": "修改后的商品名称",
       "price": 1299
     }'
   ```

3. **删除商品测试：**
   ```bash
   curl -X DELETE http://localhost:8081/items/1
   ```

#### 3.3 服务调用测试

测试 search-service 通过 Feign 调用 item-service：

观察 MQ 消息处理过程中的日志，确认：
- Feign 调用成功获取商品信息
- 商品信息正确转换为 ItemDoc
- Elasticsearch 索引操作成功

### 4. 监控与日志

#### 4.1 关键日志

**商品服务日志：**
```
[ItemController] === 商品新增成功，发送MQ消息 ===
  - 商品ID: 123
  - 商品名称: 测试商品
[ItemController] ✅ MQ消息发送成功 - item.insert
```

**搜索服务日志：**
```
[ItemListener] === 接收到商品新增/修改消息 ===
  - 商品ID: 123
[ItemListener] 📦 查询到商品信息:
  - 商品名称: 测试商品
  - 商品价格: 999
  - 商品状态: 1
[SearchService] === 新增/更新商品索引 ===
  - 商品ID: 123
  - 商品名称: 测试商品
[SearchService] ✅ 商品索引更新成功
[ItemListener] ✅ 商品索引同步成功
```

#### 4.2 Elasticsearch 验证

直接查询 Elasticsearch 验证数据：
```bash
# 查看索引
curl http://192.168.150.101:9200/_cat/indices?v

# 查询商品文档
curl http://192.168.150.101:9200/items/_search?pretty
```

### 5. 常见问题

#### 5.1 Elasticsearch 连接失败
- 检查 Elasticsearch 服务是否启动
- 检查网络连接和防火墙设置
- 验证配置中的 Elasticsearch 地址

#### 5.2 MQ 消息未接收
- 检查 RabbitMQ 服务状态
- 验证交换机和队列是否正确创建
- 检查消息路由键是否匹配

#### 5.3 Feign 调用失败
- 检查服务注册状态
- 验证服务间网络连通性
- 查看熔断器状态

## 企业级部署建议

### 1. 配置管理
- ✅ **推荐：** 使用 Nacos 配置中心统一管理配置
- ❌ **不推荐：** 本地配置文件（仅开发环境使用）

### 2. 服务治理
- 启用服务注册与发现
- 配置负载均衡策略
- 启用熔断降级机制

### 3. 监控告警
- 集成 Prometheus + Grafana 监控
- 配置 Elasticsearch 集群监控
- 设置 MQ 消息积压告警

### 4. 安全配置
- 配置 Elasticsearch 认证
- 启用 RabbitMQ 用户权限控制
- 使用 HTTPS 加密通信

## 版本信息

- **版本：** 1.0.0
- **作者：** 黑马程序员
- **更新时间：** 2024-01-15