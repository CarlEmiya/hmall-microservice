# HMall 微服务电商项目

## 项目简介

HMall是一个基于Spring Cloud微服务架构的电商平台，采用前后端分离的设计模式，实现了用户管理、商品管理、购物车、订单交易、支付等核心电商功能。

## 技术栈

### 后端技术
- **Spring Boot 2.7.12** - 微服务基础框架
- **Spring Cloud 2021.0.3** - 微服务治理框架
- **Spring Cloud Alibaba 2021.0.4.0** - 阿里巴巴微服务生态
- **Nacos** - 服务注册发现 + 配置中心
- **Spring Cloud Gateway** - API网关
- **OpenFeign** - 服务间调用
- **Sentinel** - 流量控制和熔断降级
- **Seata** - 分布式事务管理
- **MySQL 8.0.23** - 关系型数据库
- **MyBatis Plus 3.4.3** - ORM框架
- **Java 11** - 开发语言

### 部署技术
- **Docker** - 容器化部署
- **Nginx** - 反向代理和负载均衡
- **Maven** - 项目构建管理

### 开发工具
- **Lombok** - 代码简化
- **Hutool** - Java工具类库

## 项目架构

### 微服务模块
```
hmall/
├── hm-gateway/          # API网关服务
├── hm-service/          # 主业务服务
├── item-service/        # 商品服务
├── cart-service/        # 购物车服务
├── user-service/        # 用户服务
├── trade-service/       # 交易服务
├── pay-service/         # 支付服务
├── hm-api/             # 公共API接口
├── hm-common/          # 公共组件
└── hmall-nginx/        # Nginx配置
```

### 核心功能
- **用户管理**：用户注册、登录、权限管理
- **商品管理**：商品信息、库存管理、搜索功能
- **购物车**：购物车增删改查、持久化存储
- **订单交易**：订单创建、状态流转、库存扣减
- **支付系统**：支付流程、支付回调处理

## 快速开始

### 环境要求
- JDK 11+
- Maven 3.6+
- MySQL 8.0+
- Docker (可选)
- Nacos Server

### 本地运行

1. **克隆项目**
```bash
git clone <repository-url>
cd hmall
```

2. **启动基础服务**
   - 启动MySQL数据库
   - 启动Nacos服务器

3. **配置数据库**
   - 创建相应的数据库
   - 执行SQL脚本初始化数据

4. **启动微服务**
```bash
# 启动网关服务
cd hm-gateway
mvn spring-boot:run

# 启动各个微服务
cd ../item-service
mvn spring-boot:run

# 其他服务类似启动...
```

5. **启动前端**
```bash
cd hmall-nginx
# 启动nginx服务器
```

### Docker部署

```bash
# 构建镜像
docker build -t hmall-service .

# 运行容器
docker run -d -p 8080:8080 hmall-service
```

## 项目亮点

### 微服务架构设计
- 按业务领域合理拆分微服务
- 服务间通过Feign进行调用
- 统一网关管理所有服务入口

### 高可用保障
- 基于Nacos的服务注册发现
- Sentinel实现服务熔断降级
- Seata保证分布式事务一致性

### 容器化部署
- 支持Docker容器化部署
- Nginx实现负载均衡和反向代理
- 支持多环境配置管理

### 开发规范
- 统一的代码结构和命名规范
- 完善的异常处理机制
- RESTful API设计

## 接口文档

项目集成了API文档，启动后可访问：
- Swagger UI: http://localhost:8080/swagger-ui.html

## 监控管理

- **服务监控**: 通过Nacos控制台查看服务状态
- **配置管理**: 通过Nacos进行统一配置管理
- **日志管理**: 各服务日志统一收集到logs目录

## 贡献指南

1. Fork 本仓库
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 打开 Pull Request

## 许可证

本项目采用 MIT 许可证 - 查看 [LICENSE](LICENSE) 文件了解详情

## 联系方式

如有问题或建议，请通过以下方式联系：
- 提交 Issue
- 发送邮件至：hmall@example.com

---

**注意**: 本项目仅用于学习和演示目的，不建议直接用于生产环境。