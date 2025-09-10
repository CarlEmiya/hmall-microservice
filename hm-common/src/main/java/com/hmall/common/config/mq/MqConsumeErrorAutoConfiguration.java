package com.hmall.common.config.mq;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.retry.MessageRecoverer;
import org.springframework.amqp.rabbit.retry.RepublishMessageRecoverer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 消费失败消息处理自动配置类
 * 当spring.rabbitmq.listener.simple.retry.enabled=true时生效
 */
@Slf4j
@Configuration
@ConditionalOnProperty(name = "spring.rabbitmq.listener.simple.retry.enabled", havingValue = "true")
public class MqConsumeErrorAutoConfiguration {

    /**
     * 获取应用名称，用于动态生成队列名
     */
    @Value("${spring.application.name}")
    private String applicationName;

    /**
     * 声明错误消息交换机
     * @return DirectExchange
     */
    @Bean
    public DirectExchange errorDirectExchange() {
        log.info("创建错误消息交换机: error.direct");
        return new DirectExchange("error.direct", true, false);
    }

    /**
     * 声明错误消息队列，队列名为：微服务名 + ".error.queue"
     * @return Queue
     */
    @Bean
    public Queue errorQueue() {
        String queueName = applicationName + ".error.queue";
        log.info("创建错误消息队列: {}", queueName);
        return new Queue(queueName, true, false, false);
    }

    /**
     * 绑定错误队列到错误交换机，RoutingKey为微服务名
     * @param errorQueue 错误队列
     * @param errorDirectExchange 错误交换机
     * @return Binding
     */
    @Bean
    public Binding errorQueueBinding(Queue errorQueue, DirectExchange errorDirectExchange) {
        log.info("绑定错误队列到交换机，RoutingKey: {}", applicationName);
        return BindingBuilder.bind(errorQueue).to(errorDirectExchange).with(applicationName);
    }

    /**
     * 声明RepublishMessageRecoverer，消费失败的消息会被投递到错误交换机
     * @param rabbitTemplate RabbitTemplate
     * @return MessageRecoverer
     */
    @Bean
    public MessageRecoverer republishMessageRecoverer(RabbitTemplate rabbitTemplate) {
        log.info("创建RepublishMessageRecoverer，错误消息将投递到error.direct交换机，RoutingKey: {}", applicationName);
        return new RepublishMessageRecoverer(rabbitTemplate, "error.direct", applicationName);
    }
}