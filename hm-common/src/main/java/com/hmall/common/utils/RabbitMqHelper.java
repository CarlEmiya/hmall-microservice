package com.hmall.common.utils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class RabbitMqHelper {

    private final RabbitTemplate rabbitTemplate;

    /**
     * 发送普通消息
     * @param exchange 交换机名称
     * @param routingKey 路由键
     * @param msg 消息内容
     */
    public void sendMessage(String exchange, String routingKey, Object msg){
        System.out.println("[RabbitMQ-普通消息] 准备发送消息");
        System.out.println("  - Exchange: " + exchange);
        System.out.println("  - RoutingKey: " + routingKey);
        System.out.println("  - Message: " + msg);
        log.info("准备发送消息，exchange: {}, routingKey: {}, message: {}", exchange, routingKey, msg);
        rabbitTemplate.convertAndSend(exchange, routingKey, msg);
        System.out.println("[RabbitMQ-普通消息] 消息已发送到RabbitMQ");
    }

    /**
     * 发送延迟消息
     * @param exchange 交换机名称
     * @param routingKey 路由键
     * @param msg 消息内容
     * @param delay 延迟时间（毫秒）
     */
    public void sendDelayMessage(String exchange, String routingKey, Object msg, int delay){
        System.out.println("[RabbitMQ-延迟消息] 准备发送延迟消息");
        System.out.println("  - Exchange: " + exchange);
        System.out.println("  - RoutingKey: " + routingKey);
        System.out.println("  - Message: " + msg);
        System.out.println("  - Delay: " + delay + "ms");
        log.info("准备发送延迟消息，exchange: {}, routingKey: {}, message: {}, delay: {}ms", exchange, routingKey, msg, delay);
        rabbitTemplate.convertAndSend(exchange, routingKey, msg, new MessagePostProcessor() {
            @Override
            public Message postProcessMessage(Message message) throws AmqpException {
                message.getMessageProperties().setDelay(delay);
                return message;
            }
        });
        System.out.println("[RabbitMQ-延迟消息] 延迟消息已发送到RabbitMQ，将在 " + delay + "ms 后投递");
    }

    /**
     * 发送消息并等待确认
     * @param exchange 交换机名称
     * @param routingKey 路由键
     * @param msg 消息内容
     * @param maxRetries 最大重试次数
     */
    public void sendMessageWithConfirm(String exchange, String routingKey, Object msg, int maxRetries){
        System.out.println("[RabbitMQ-发布者确认] 准备发送确认消息");
        System.out.println("  - Exchange: " + exchange);
        System.out.println("  - RoutingKey: " + routingKey);
        System.out.println("  - Message: " + msg);
        System.out.println("  - MaxRetries: " + maxRetries);
        log.info("准备发送确认消息，exchange: {}, routingKey: {}, message: {}, maxRetries: {}", exchange, routingKey, msg, maxRetries);
        
        // 生成唯一的消息ID
        String messageId = UUID.randomUUID().toString();
        System.out.println("  - MessageId: " + messageId);
        CorrelationData correlationData = new CorrelationData(messageId);
        
        // 设置确认回调
        correlationData.getFuture().addCallback(new ListenableFutureCallback<CorrelationData.Confirm>() {
            @Override
            public void onFailure(Throwable ex) {
                System.out.println("[RabbitMQ-发布者确认] ❌ 消息发送失败");
                System.out.println("  - MessageId: " + messageId);
                System.out.println("  - Error: " + ex.getMessage());
                log.error("消息发送失败，messageId: {}, error: {}", messageId, ex.getMessage());
                // 重试逻辑
                retryMessage(exchange, routingKey, msg, maxRetries - 1);
            }

            @Override
            public void onSuccess(CorrelationData.Confirm result) {
                if (result.isAck()) {
                    System.out.println("[RabbitMQ-发布者确认] ✅ 消息发送成功");
                    System.out.println("  - MessageId: " + messageId);
                    System.out.println("  - Broker已确认接收消息");
                    log.info("消息发送成功，messageId: {}", messageId);
                } else {
                    System.out.println("[RabbitMQ-发布者确认] ❌ 消息发送被拒绝");
                    System.out.println("  - MessageId: " + messageId);
                    System.out.println("  - Reason: " + result.getReason());
                    log.error("消息发送被拒绝，messageId: {}, reason: {}", messageId, result.getReason());
                    // 重试逻辑
                    retryMessage(exchange, routingKey, msg, maxRetries - 1);
                }
            }
        });
        
        System.out.println("[RabbitMQ-发布者确认] 消息已发送到RabbitMQ，等待确认...");
        rabbitTemplate.convertAndSend(exchange, routingKey, msg, correlationData);
    }
    
    /**
     * 重试发送消息
     * @param exchange 交换机名称
     * @param routingKey 路由键
     * @param msg 消息内容
     * @param retries 剩余重试次数
     */
    private void retryMessage(String exchange, String routingKey, Object msg, int retries) {
        if (retries > 0) {
            System.out.println("[RabbitMQ-重试机制] 🔄 开始重试发送消息");
            System.out.println("  - 剩余重试次数: " + retries);
            System.out.println("  - Exchange: " + exchange);
            System.out.println("  - RoutingKey: " + routingKey);
            log.info("重试发送消息，剩余重试次数: {}", retries);
            sendMessageWithConfirm(exchange, routingKey, msg, retries);
        } else {
            System.out.println("[RabbitMQ-重试机制] ❌ 消息发送最终失败");
            System.out.println("  - 已达到最大重试次数");
            System.out.println("  - Exchange: " + exchange);
            System.out.println("  - RoutingKey: " + routingKey);
            System.out.println("  - Message: " + msg);
            log.error("消息发送失败，已达到最大重试次数，exchange: {}, routingKey: {}, message: {}", exchange, routingKey, msg);
        }
    }
}