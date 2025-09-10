# HMall项目RabbitMQ高级特性改造任务清单

## 基于RabbitMQ高级特性的项目改造建议

### 1. 生产者确认机制(Publisher Confirm)改造

#### 1.1 配置文件修改
**文件**: `nacos配置中心 - shared-mq.yaml`
**修改内容**: 启用生产者确认机制
```yaml
spring:
  rabbitmq:
    # 现有配置保持不变...
    publisher-confirm-type: correlated # 开启publisher confirm机制
    publisher-returns: true # 开启publisher return机制
    template:
      mandatory: true # 启用失败回调
```

#### 1.2 MqConfig配置类增强
**文件**: `hm-common/src/main/java/com/hmall/common/config/MqConfig.java`
**修改内容**: 添加ConfirmCallback配置
- 在现有RabbitTemplate Bean中添加ConfirmCallback
- 实现消息发送成功/失败的回调处理

#### 1.3 消息发送方法改造
**文件**: `trade-service/src/main/java/com/hmall/trade/service/impl/OrderServiceImpl.java`
**修改内容**: 使用CorrelationData发送消息
- 为每条消息生成唯一ID
- 添加发送结果回调处理

### 2. 消费者确认机制改造

#### 2.1 配置文件修改
**文件**: `nacos配置中心 - shared-mq.yaml`
**修改内容**: 配置手动确认模式
```yaml
spring:
  rabbitmq:
    listener:
      simple:
        acknowledge-mode: manual # 改为手动确认模式
```

#### 2.2 CartListener消费者改造
**文件**: `cart-service/src/main/java/com/hmall/cart/lintener/CartListener.java`
**修改内容**: 实现手动确认机制
- 添加Channel和DeliveryTag参数
- 成功处理后调用basicAck
- 异常时调用basicNack并重新入队

### 3. 死信队列机制实现

#### 3.1 死信队列配置类
**文件**: `hm-common/src/main/java/com/hmall/common/config/DeadLetterConfig.java` (新建)
**内容**: 创建死信队列配置
- 定义死信交换机 dead.letter.topic
- 定义死信队列 dead.letter.queue
- 为业务队列添加死信配置

#### 3.2 死信消息处理器
**文件**: `hm-common/src/main/java/com/hmall/common/listener/DeadLetterListener.java` (新建)
**内容**: 处理死信消息
- 监听死信队列
- 记录死信消息日志
- 实现告警通知机制

### 4. 消息幂等性保证

#### 4.1 幂等性工具类
**文件**: `hm-common/src/main/java/com/hmall/common/utils/IdempotentUtils.java` (新建)
**内容**: 消息幂等性处理工具
- 基于Redis实现消息去重
- 提供消息处理锁机制
- 支持TTL过期清理

#### 4.2 CartListener幂等性改造
**文件**: `cart-service/src/main/java/com/hmall/cart/lintener/CartListener.java`
**修改内容**: 添加幂等性检查
- 消息处理前检查是否已处理
- 处理完成后标记消息状态
- 避免重复消费问题

### 5. 消息持久化确保

#### 5.1 交换机和队列持久化检查
**文件**: `hm-common/src/main/java/com/hmall/common/config/RabbitMQConfig.java` (新建)
**内容**: 确保所有交换机和队列都是持久化的
- 显式声明交换机持久化
- 显式声明队列持久化
- 确保消息持久化配置

### 6. 监控和告警机制

#### 6.1 消息监控配置
**文件**: `hm-common/src/main/java/com/hmall/common/monitor/MessageMonitor.java` (新建)
**内容**: 消息处理监控
- 统计消息发送数量
- 统计消息消费数量
- 记录消息处理失败情况
- 基于Redis存储监控数据

### 7. 延迟消息实现

#### 7.1 延迟队列配置
**文件**: `hm-common/src/main/java/com/hmall/common/config/DelayQueueConfig.java` (新建)
**内容**: 延迟消息队列配置
- 实现订单超时处理
- 基于TTL+死信实现延迟
- 支持动态延迟时间

## 改造优先级和时间安排

### 高优先级（第1周）
1. ✅ 生产者确认机制配置和实现
2. ✅ 消费者手动确认机制改造
3. ✅ 消息幂等性保证实现

### 中优先级（第2周）
4. ⏳ 死信队列机制实现
5. ⏳ 消息持久化配置检查和完善
6. ⏳ 基础监控机制实现

### 低优先级（第3周）
7. ⏸️ 延迟消息机制实现
8. ⏸️ 高级监控和告警机制
9. ⏸️ 性能优化和压力测试

## 具体实施步骤

### 步骤1: Nacos配置更新
- 登录Nacos控制台
- 修改shared-mq.yaml配置
- 添加publisher-confirm-type和publisher-returns配置
- 修改acknowledge-mode为manual

### 步骤2: 公共组件改造
- 修改MqConfig.java添加确认回调
- 创建IdempotentUtils.java工具类
- 创建DeadLetterConfig.java配置类
- 创建MessageMonitor.java监控类

### 步骤3: 业务服务改造
- 修改OrderServiceImpl.java消息发送逻辑
- 修改CartListener.java消费逻辑
- 添加手动确认和幂等性处理

### 步骤4: 测试验证
- 功能测试：正常消息流程
- 异常测试：网络中断、服务宕机
- 性能测试：高并发场景
- 监控验证：查看统计数据

## 注意事项

1. **配置变更**: 在Nacos中统一管理配置，确保多环境一致性
2. **向下兼容**: 改造过程中确保现有功能正常运行
3. **监控告警**: 密切关注系统运行状态和性能指标
4. **回滚方案**: 每个步骤都准备相应的回滚策略
5. **文档更新**: 及时更新技术文档和操作手册

## 预期收益

1. **可靠性提升**: 消息零丢失，处理零重复
2. **监控完善**: 实时掌握消息处理状态和性能
3. **故障恢复**: 快速定位和解决消息相关问题
4. **性能优化**: 合理的重试和死信机制避免资源浪费
5. **运维友好**: 完善的监控告警机制提升运维效率

---

**创建时间**: 2024年1月
**负责人**: 开发团队
**预计完成时间**: 3周
**当前状态**: 规划阶段