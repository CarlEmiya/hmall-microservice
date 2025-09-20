# HMall 电商微服务项目

## 项目简介

HMall 是一个基于 Spring Cloud 微服务架构的电商平台，采用现代化的技术栈构建，实现了商品管理、用户管理、购物车、订单交易、支付等核心电商功能。项目遵循微服务设计原则，具有高可用、高并发、易扩展的特点。

## 技术架构

### 核心技术栈

- **开发语言**: Java 11
- **框架版本**: Spring Boot 2.7.12, Spring Cloud 2021.0.3
- **微服务组件**: Spring Cloud Alibaba 2021.0.4.0
- **数据库**: MySQL 8.0.23
- **缓存**: Redis (支持哨兵模式)
- **搜索引擎**: Elasticsearch 7.12.1
- **消息队列**: RabbitMQ
- **ORM框架**: MyBatis Plus 3.4.3
- **工具库**: Hutool 5.8.11

### 微服务组件

- **服务注册与发现**: Nacos
- **配置管理**: Nacos Config
- **API网关**: Spring Cloud Gateway
- **服务调用**: OpenFeign + OkHttp
- **负载均衡**: Spring Cloud LoadBalancer
- **熔断限流**: Sentinel
- **分布式事务**: Seata
- **API文档**: Swagger/Knife4j

## 项目结构

```
hmall/
├── hm-common/           # 公共模块 - 通用工具类、异常处理、缓存工具等
├── hm-api/              # API模块 - Feign客户端接口定义
├── hm-gateway/          # 网关服务 - 统一入口、路由转发、认证鉴权
├── item-service/        # 商品服务 - 商品管理、库存管理、商品搜索
├── user-service/        # 用户服务 - 用户注册登录、个人信息管理
├── cart-service/        # 购物车服务 - 购物车增删改查、缓存优化
├── trade-service/       # 交易服务 - 订单管理、订单状态流转
├── pay-service/         # 支付服务 - 支付处理、支付回调
├── search-service/      # 搜索服务 - 基于Elasticsearch的商品搜索
└── hm-service/          # 单体服务 - 早期版本的单体应用
```

## 核心功能

### 1. 商品管理 (item-service)
- **端口**: 8081
- **功能**: 
  - 商品信息的增删改查
  - 商品分页查询和条件筛选
  - 商品库存管理
  - 商品状态管理
  - 缓存优化 (Cache Aside模式)
- **技术特点**: 
  - 使用Redis缓存提升查询性能
  - 支持商品信息的实时更新
  - 集成RabbitMQ实现数据同步

### 2. 用户管理 (user-service)
- **端口**: 8084
- **功能**:
  - 用户注册、登录、注销
  - 用户信息管理
  - 用户余额管理
  - JWT令牌认证
- **技术特点**:
  - 基于JWT的无状态认证
  - 支持用户信息缓存
  - 集成Sentinel熔断保护

### 3. 购物车管理 (cart-service)
- **端口**: 8082
- **功能**:
  - 购物车商品增删改查
  - 购物车数据持久化
  - 购物车缓存优化
  - 批量操作支持
- **技术特点**:
  - Redis缓存提升响应速度
  - 支持用户登录状态下的购物车同步
  - 优化的缓存更新策略

### 4. 订单交易 (trade-service)
- **端口**: 8083
- **功能**:
  - 订单创建和管理
  - 订单状态流转
  - 订单支付集成
  - 分布式事务处理
- **技术特点**:
  - 基于Seata的分布式事务
  - 支持订单状态机管理
  - 集成消息队列确保数据一致性

### 5. 支付服务 (pay-service)
- **端口**: 8085
- **功能**:
  - 支付订单处理
  - 支付状态管理
  - 支付回调处理
  - 多种支付方式支持
- **技术特点**:
  - 异步支付处理
  - 支付结果通知机制
  - 支付安全保障

### 6. 搜索服务 (search-service)
- **端口**: 8087
- **功能**:
  - 基于Elasticsearch的商品搜索
  - 多条件组合搜索
  - 搜索结果排序
  - 搜索性能优化
- **技术特点**:
  - 全文检索支持
  - 实时数据同步
  - 搜索结果缓存

### 7. API网关 (hm-gateway)
- **端口**: 8080
- **功能**:
  - 统一API入口
  - 路由转发
  - 负载均衡
  - 认证鉴权
  - 限流熔断
- **技术特点**:
  - 基于Spring Cloud Gateway
  - 支持动态路由配置
  - 集成Sentinel流量控制

## 缓存架构

项目采用多层缓存架构，显著提升系统性能：

### 缓存策略
- **Cache Aside模式**: 商品信息缓存
- **延迟双删**: 数据一致性保障
- **互斥锁**: 防止缓存击穿
- **布隆过滤器**: 防止缓存穿透

### 缓存应用
- **商品服务**: 商品详情、商品列表缓存
- **购物车服务**: 用户购物车数据缓存
- **搜索服务**: 搜索结果缓存
- **用户服务**: 用户信息缓存

## 消息队列

使用RabbitMQ实现异步处理和服务解耦：

- **商品数据同步**: 商品信息变更时同步到搜索服务
- **订单状态通知**: 订单状态变更通知相关服务
- **支付结果处理**: 异步处理支付结果
- **库存扣减**: 异步处理库存变更

## 监控与治理

### 服务治理
- **Nacos**: 服务注册发现、配置管理
- **Sentinel**: 流量控制、熔断降级
- **Seata**: 分布式事务管理

### 配置管理
- 统一配置中心 (Nacos Config)
- 环境隔离 (dev/test/prod)
- 配置热更新支持

## 部署架构

### 服务端口分配
- hm-gateway: 8080 (网关)
- item-service: 8081 (商品服务)
- cart-service: 8082 (购物车服务)
- trade-service: 8083 (交易服务)
- user-service: 8084 (用户服务)
- pay-service: 8085 (支付服务)
- search-service: 8087 (搜索服务)

### 基础设施
- **Nacos**: 192.168.80.129:8848 (服务注册中心)
- **MySQL**: 多数据库实例 (hm-item, hm-cart, hm-trade, hm-pay, hm-user)
- **Redis**: 支持哨兵模式的高可用缓存
- **Elasticsearch**: 192.168.80.129:9200 (搜索引擎)
- **Sentinel Dashboard**: localhost:8090 (监控面板)

## 项目特色

### 1. 高性能缓存方案
- 多级缓存架构
- 智能缓存更新策略
- 缓存穿透、击穿、雪崩防护

### 2. 分布式事务处理
- 基于Seata的AT模式
- 事务补偿机制
- 数据最终一致性保障

### 3. 服务容错设计
- Sentinel熔断限流
- 服务降级策略
- 超时重试机制

### 4. 数据一致性
- 分布式锁
- 消息队列异步处理
- 最终一致性保障

### 5. 可观测性
- 完整的日志体系
- 服务监控指标
- 链路追踪支持

## 数据库初始化

项目的所有SQL文件都位于 `sql/` 文件夹下，包含：
- `hm-cart.sql` - 购物车服务数据库
- `hm-item.sql` - 商品服务数据库  
- `hm-pay.sql` - 支付服务数据库
- `hm-trade.sql` - 交易服务数据库
- `hm-user.sql` - 用户服务数据库
- `nacos.sql` - Nacos配置中心数据库
- `seata.sql` - Seata分布式事务数据库

## 部署环境

### Docker容器部署

所有基础服务都部署在虚拟机的Docker容器中，连接在同一个网络 `hmall` 中，数据都挂载在对应的数据卷中：

```bash
# 当前运行的容器服务
CONTAINER ID   IMAGE                         PORTS                                           STATUS          NAMES 
cef4870402c8   redis                                                                         Up              s1 (Redis哨兵1)
6728d4492a3f   redis                                                                         Up              r2 (Redis从节点2)
e8c38a28db3a   redis                                                                         Up              s3 (Redis哨兵3)
cf03460f75f0   redis                                                                         Up              s2 (Redis哨兵2)
4ff348ca7258   redis                                                                         Up              r1 (Redis从节点1)
15f3550e2b2c   redis                                                                         Up              r3 (Redis主节点)
5599a22e5a8a   mysql                         0.0.0.0:3307->3306/tcp                         Up              mysql-9.4.0
41e4ffbede79   mysql:8.0.28                  0.0.0.0:3306->3306/tcp                         Up              mysql
303304e7fdd3   kibana:7.12.1                 0.0.0.0:5601->5601/tcp                         Up              kibana
0f4850c5dce5   elasticsearch:7.12.1          0.0.0.0:9200->9200/tcp, 9300->9300/tcp         Up              es
793c4111e4dd   rabbitmq:3.8-management       0.0.0.0:5672->5672/tcp, 15672->15672/tcp       Up              mq
a93d84a7969e   nacos/nacos-server:v2.2.0.1   0.0.0.0:8848->8848/tcp, 9848-9849->9848-9849/tcp Up            nacos
483021a180f7   seataio/seata-server:1.5.2    0.0.0.0:7099->7099/tcp, 8099->8099/tcp         Up              seata
```

### 服务访问地址

- **RabbitMQ管理界面**: `http://虚拟机IP:15672`
  - 账号: `itheima`
  - 密码: `123321`
  - 注意：想看控制台的可以创建一下角色hmall，密码123，其他的本系统可以自行创建

- **Elasticsearch**: `http://虚拟机IP:5601/app/dev_tools#/console`

- **Nacos控制台**: `http://虚拟机IP:8848/nacos`
  - 账号: `nacos`
  - 密码: `nacos`
  - 注意：数据库中只保留了结构，配置需要自己写一下，不会的可以对着教程找

- **前端页面**: 
  - 将 `hmall-nginx` 放在无中文路径下
  - 双击 `hmall-nginx\nginx.exe` 启动
  - 访问地址: `http://localhost`

- **教程地址**: 
  - 访问地址: `https://b11et3un53m.feishu.cn/wiki/FYNkwb1i6i0qwCk7lF2caEq5nRe`

## 快速开始

### 环境要求
- JDK 11+
- Maven 3.6+
- Docker & Docker Compose
- 虚拟机环境（用于部署基础服务）

### 启动步骤

1. **部署基础服务**
   ```bash
   # 在虚拟机中启动所有Docker容器
   # 确保所有容器都在hmall网络中
   ```

2. **初始化数据库**
   ```bash
   # 导入sql文件夹下的所有SQL脚本到对应数据库
   mysql -u root -p < sql/hm-user.sql
   mysql -u root -p < sql/hm-item.sql
   # ... 导入其他SQL文件
   ```

3. **配置Nacos**
   - 访问Nacos控制台导入配置文件
   - 配置各服务的数据库连接信息

4. **启动微服务**
   ```bash
   # 启动各微服务
   mvn spring-boot:run -pl user-service
   mvn spring-boot:run -pl item-service
   mvn spring-boot:run -pl cart-service
   mvn spring-boot:run -pl trade-service
   mvn spring-boot:run -pl pay-service
   mvn spring-boot:run -pl search-service
   mvn spring-boot:run -pl hm-gateway
   ```

5. **启动前端**
   ```bash
   # 启动Nginx
   cd hmall-nginx
   ./nginx.exe
   ```

6. **访问应用**
   - 前端页面: `http://localhost`
   - API文档: `http://localhost:8080/doc.html`

## 常见问题排查

### Elasticsearch数据导入问题

如果发现Elasticsearch中有hmall索引但搜索无结果，可能是数据为空，解决方案：

1. **手动导入数据**
   - 访问: `http://localhost:8082/search/list/import`
   - 额外注意：如果报错`401`，先去登录一下，账号`jack`，密码`123`，数据库里的是加密的
   - 或调用 <mcfile name="DataImportController.java" path="search-service\src\main\java\com\hmall\search\controller\DataImportController.java"></mcfile> 中的 `importItems()` 方法

2. **补充**
   - 该Controller还提供了其他数据操作方法供调试使用

### 学习资源

- **项目教程**: https://b11et3un53m.feishu.cn/wiki/FYNkwb1i6i0qwCk7lF2caEq5nRe
- **教学资源**: 本来准备了的，怕有法律风险，自己去黑马的地址下吧：`https://pan.baidu.com/s/1JX0fhmV82mgPeBBbAMNV0w&pwd=7988`

### 注意事项

- Docker镜像需要自行构建，项目中未提供预制镜像
- Nacos数据库中只保留了表结构，配置数据需要重新导入
- 确保所有服务都能正常连接到虚拟机中的基础服务

## 开发规范

- 遵循RESTful API设计规范
- 统一的异常处理机制
- 完善的参数校验
- 标准的返回结果封装
- 详细的API文档

## 项目亮点

1. **微服务架构**: 采用Spring Cloud生态，实现服务的高内聚、低耦合
2. **缓存优化**: 多层缓存架构，显著提升系统性能
3. **分布式事务**: 基于Seata实现跨服务的事务一致性
4. **服务治理**: 完善的服务注册发现、配置管理、流量控制
5. **高可用设计**: 熔断降级、超时重试、服务容错
6. **数据一致性**: 多种策略保障分布式环境下的数据一致性
7. **可扩展性**: 模块化设计，支持水平扩展

---

*该项目是一个完整的微服务电商解决方案，适合学习Spring Cloud微服务架构和分布式系统设计。*