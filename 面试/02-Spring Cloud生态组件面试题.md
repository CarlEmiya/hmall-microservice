# Spring Cloud生态组件面试题

## 1. 请介绍一下Nacos的作用，以及在项目中是如何使用的？

### 回答要点：

**Nacos核心功能：**
1. **服务注册与发现**：替代Eureka，提供服务注册中心功能
2. **配置管理**：集中管理各服务的配置文件
3. **动态配置推送**：配置变更实时推送到各服务实例

**项目中的应用：**

**1. 服务注册发现配置：**
```yaml
# bootstrap.yaml
spring:
  application:
    name: user-service
  cloud:
    nacos:
      discovery:
        server-addr: 192.168.150.101:8848
        namespace: dev
        group: hmall
      config:
        server-addr: 192.168.150.101:8848
        namespace: dev
        group: hmall
        file-extension: yaml
```

**2. 配置管理实践：**
- **共享配置**：`shared-jdbc.yaml`、`shared-log.yaml`等公共配置
- **环境隔离**：通过namespace区分dev、test、prod环境
- **配置分组**：使用group对不同业务线配置进行分组

**3. 动态配置刷新：**
```java
@Component
@RefreshScope  // 支持配置热刷新
public class PayConfig {
    @Value("${pay.alipay.appId}")
    private String appId;
    
    @Value("${pay.alipay.privateKey}")
    private String privateKey;
}
```

**Nacos优势：**
- **高可用**：支持集群部署，数据持久化到MySQL
- **多语言支持**：Java、Go、Python等多语言SDK
- **权限控制**：支持用户权限管理和访问控制
- **可视化管理**：提供Web控制台，操作简便

---

## 2. Spring Cloud Gateway的工作原理是什么？如何实现路由和过滤？

### 回答要点：

**Gateway核心概念：**
1. **Route（路由）**：请求的转发规则
2. **Predicate（断言）**：路由匹配条件
3. **Filter（过滤器）**：请求处理逻辑

**工作流程：**
```
客户端请求 → Gateway → 断言匹配 → 过滤器链 → 目标服务
```

**项目中的配置：**
```yaml
spring:
  cloud:
    gateway:
      routes:
        - id: user-service
          uri: lb://user-service
          predicates:
            - Path=/users/**,/address/**
          filters:
            - StripPrefix=0
        - id: item-service
          uri: lb://item-service
          predicates:
            - Path=/items/**,/search/**
        - id: cart-service
          uri: lb://cart-service
          predicates:
            - Path=/carts/**
        - id: trade-service
          uri: lb://trade-service
          predicates:
            - Path=/orders/**
        - id: pay-service
          uri: lb://pay-service
          predicates:
            - Path=/pay/**
      default-filters:
        - AddRequestHeader=Truth,Itcast is freaking awesome!
      globalcors:
        cors-configurations:
          '[/**]':
            allowedOriginPatterns: "*"
            allowedMethods: "*"
            allowedHeaders: "*"
            allowCredentials: true
            maxAge: 360000
```

**自定义过滤器实现：**
```java
@Component
public class AuthGlobalFilter implements GlobalFilter, Ordered {
    
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 1. 获取请求路径
        String path = exchange.getRequest().getPath().toString();
        
        // 2. 判断是否需要登录拦截
        if (isExcludePath(path)) {
            return chain.filter(exchange);
        }
        
        // 3. 获取token
        String token = getToken(exchange.getRequest());
        
        // 4. 校验token
        if (StringUtils.isBlank(token)) {
            return unauthorizedResponse(exchange, "请先登录");
        }
        
        try {
            // 5. 解析token，获取用户信息
            Long userId = JwtTool.parseToken(token);
            
            // 6. 传递用户信息到下游服务
            ServerHttpRequest request = exchange.getRequest().mutate()
                .header("user-info", userId.toString())
                .build();
            
            return chain.filter(exchange.mutate().request(request).build());
        } catch (Exception e) {
            return unauthorizedResponse(exchange, "token无效");
        }
    }
    
    @Override
    public int getOrder() {
        return 0;
    }
}
```

**Gateway优势：**
- **性能优异**：基于WebFlux，支持高并发
- **功能丰富**：内置多种断言和过滤器
- **易于扩展**：支持自定义过滤器
- **Spring生态**：与Spring Cloud无缝集成

---

## 3. Sentinel是如何实现熔断降级的？在项目中如何配置？

### 回答要点：

**Sentinel核心功能：**
1. **流量控制**：限制QPS，防止系统过载
2. **熔断降级**：服务异常时快速失败，保护系统
3. **系统负载保护**：根据系统负载自动调节流量
4. **热点参数限流**：针对热点数据进行限流

**熔断降级原理：**
```
正常状态 → 异常率/响应时间超阈值 → 熔断状态 → 探测恢复 → 正常状态
```

**项目中的配置：**

**1. 依赖引入：**
```xml
<dependency>
    <groupId>com.alibaba.cloud</groupId>
    <artifactId>spring-cloud-starter-alibaba-sentinel</artifactId>
</dependency>
```

**2. 配置文件：**
```yaml
spring:
  cloud:
    sentinel:
      transport:
        dashboard: 192.168.150.101:8858
        port: 8719
      datasource:
        flow:
          nacos:
            server-addr: 192.168.150.101:8848
            dataId: orderservice-flow-rules
            groupId: SENTINEL_GROUP
            rule-type: flow
```

**3. 服务降级实现：**
```java
@Service
public class ItemService {
    
    @Autowired
    private ItemClient itemClient;
    
    @SentinelResource(
        value = "getItemById",
        blockHandler = "getItemByIdBlockHandler",
        fallback = "getItemByIdFallback"
    )
    public ItemDTO getItemById(Long id) {
        return itemClient.getById(id);
    }
    
    // 限流降级处理
    public ItemDTO getItemByIdBlockHandler(Long id, BlockException e) {
        log.warn("商品查询被限流，id: {}", id);
        return createDefaultItem(id);
    }
    
    // 异常降级处理
    public ItemDTO getItemByIdFallback(Long id, Throwable e) {
        log.error("商品查询异常，id: {}", id, e);
        return createDefaultItem(id);
    }
    
    private ItemDTO createDefaultItem(Long id) {
        ItemDTO item = new ItemDTO();
        item.setId(id);
        item.setName("商品暂时无法获取");
        item.setPrice(0);
        return item;
    }
}
```

**4. OpenFeign集成：**
```java
@FeignClient(value = "item-service", fallbackFactory = ItemClientFallback.class)
public interface ItemClient {
    @GetMapping("/items/{id}")
    ItemDTO getById(@PathVariable Long id);
}

@Component
public class ItemClientFallback implements FallbackFactory<ItemClient> {
    @Override
    public ItemClient create(Throwable cause) {
        return new ItemClient() {
            @Override
            public ItemDTO getById(Long id) {
                log.error("商品服务调用失败", cause);
                return createDefaultItem(id);
            }
        };
    }
}
```

**规则配置策略：**
- **QPS限流**：根据历史访问量设置合理阈值
- **异常比例熔断**：异常率超过50%时熔断
- **响应时间熔断**：RT超过1000ms时熔断
- **热点参数限流**：对商品ID等热点参数限流

---

## 4. OpenFeign是如何实现服务间调用的？有哪些优化配置？

### 回答要点：

**OpenFeign工作原理：**
1. **动态代理**：基于接口生成代理对象
2. **请求构建**：将方法调用转换为HTTP请求
3. **负载均衡**：集成LoadBalancer实现负载均衡
4. **服务发现**：从注册中心获取服务实例列表

**项目中的使用：**

**1. 基础配置：**
```java
@FeignClient(name = "item-service")
public interface ItemClient {
    @GetMapping("/items/{id}")
    ItemDTO getById(@PathVariable Long id);
    
    @PostMapping("/items/stock/deduct")
    void deductStock(@RequestBody List<OrderDetailDTO> items);
}
```

**2. 全局配置：**
```yaml
feign:
  client:
    config:
      default:
        loggerLevel: BASIC
        connectTimeout: 5000
        readTimeout: 10000
  httpclient:
    enabled: true
    max-connections: 200
    max-connections-per-route: 50
  compression:
    request:
      enabled: true
      mime-types: text/xml,application/xml,application/json
      min-request-size: 2048
    response:
      enabled: true
```

**3. 自定义配置类：**
```java
@Configuration
public class FeignConfig {
    
    @Bean
    public Logger.Level feignLogLevel() {
        return Logger.Level.FULL;
    }
    
    @Bean
    public RequestInterceptor requestInterceptor() {
        return template -> {
            // 传递用户信息
            Long userId = UserContext.getUser();
            if (userId != null) {
                template.header("user-info", userId.toString());
            }
        };
    }
    
    @Bean
    public Retryer retryer() {
        return new Retryer.Default(100, 1000, 3);
    }
}
```

**4. 性能优化配置：**
```java
@Configuration
public class FeignHttpConfig {
    
    @Bean
    public OkHttpClient okHttpClient() {
        return new OkHttpClient.Builder()
            .connectTimeout(5, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .connectionPool(new ConnectionPool(50, 5, TimeUnit.MINUTES))
            .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BASIC))
            .build();
    }
}
```

**最佳实践：**
- **接口设计**：遵循RESTful规范，参数简洁明确
- **超时配置**：根据业务场景设置合理超时时间
- **重试机制**：配置重试策略，避免网络抖动影响
- **连接池**：使用连接池提高性能
- **日志记录**：合理配置日志级别，便于问题排查

---

## 5. LoadBalancer负载均衡策略有哪些？如何自定义？

### 回答要点：

**LoadBalancer内置策略：**
1. **RoundRobinLoadBalancer**：轮询（默认）
2. **RandomLoadBalancer**：随机选择
3. **WeightedResponseTimeLoadBalancer**：响应时间加权

**项目中的配置：**

**1. 全局配置：**
```yaml
spring:
  cloud:
    loadbalancer:
      ribbon:
        enabled: false
      cache:
        enabled: false
```

**2. 自定义负载均衡策略：**
```java
@Configuration
public class LoadBalancerConfig {
    
    @Bean
    @LoadBalanced
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
    
    @Bean
    public ReactorLoadBalancer<ServiceInstance> customLoadBalancer(
            Environment environment,
            LoadBalancerClientFactory loadBalancerClientFactory) {
        String name = environment.getProperty(LoadBalancerClientFactory.PROPERTY_NAME);
        return new CustomLoadBalancer(
            loadBalancerClientFactory.getLazyProvider(name, ServiceInstanceListSupplier.class),
            name
        );
    }
}

// 自定义负载均衡实现
public class CustomLoadBalancer implements ReactorServiceInstanceLoadBalancer {
    
    private final AtomicInteger position = new AtomicInteger(0);
    private final String serviceId;
    private final ObjectProvider<ServiceInstanceListSupplier> serviceInstanceListSupplierProvider;
    
    @Override
    public Mono<Response<ServiceInstance>> choose(Request request) {
        ServiceInstanceListSupplier supplier = serviceInstanceListSupplierProvider
            .getIfAvailable(NoopServiceInstanceListSupplier::new);
        
        return supplier.get(request)
            .next()
            .map(serviceInstances -> processInstanceResponse(serviceInstances, request));
    }
    
    private Response<ServiceInstance> processInstanceResponse(
            List<ServiceInstance> serviceInstances, Request request) {
        
        if (serviceInstances.isEmpty()) {
            return new EmptyResponse();
        }
        
        // 自定义负载均衡逻辑：优先选择本地实例
        ServiceInstance instance = getLocalInstance(serviceInstances);
        if (instance == null) {
            // 如果没有本地实例，使用轮询
            int pos = Math.abs(this.position.incrementAndGet());
            instance = serviceInstances.get(pos % serviceInstances.size());
        }
        
        return new DefaultResponse(instance);
    }
    
    private ServiceInstance getLocalInstance(List<ServiceInstance> instances) {
        String localHost = getLocalHost();
        return instances.stream()
            .filter(instance -> localHost.equals(instance.getHost()))
            .findFirst()
            .orElse(null);
    }
}
```

**3. 针对特定服务配置：**
```java
@Configuration
public class ItemServiceLoadBalancerConfig {
    
    @Bean
    @ConditionalOnProperty(name = "spring.application.name", havingValue = "item-service")
    public ReactorLoadBalancer<ServiceInstance> itemServiceLoadBalancer(
            Environment environment,
            LoadBalancerClientFactory loadBalancerClientFactory) {
        String name = environment.getProperty(LoadBalancerClientFactory.PROPERTY_NAME);
        return new RandomLoadBalancer(
            loadBalancerClientFactory.getLazyProvider(name, ServiceInstanceListSupplier.class),
            name
        );
    }
}
```

**负载均衡策略选择：**
- **轮询**：适合服务实例性能相近的场景
- **随机**：简单有效，适合大多数场景
- **加权轮询**：根据服务器性能分配权重
- **最少连接**：适合长连接场景
- **一致性哈希**：适合缓存场景，保证数据亲和性

**监控指标：**
- 各实例请求分布是否均匀
- 响应时间是否在合理范围
- 错误率是否正常
- 实例健康状态检查