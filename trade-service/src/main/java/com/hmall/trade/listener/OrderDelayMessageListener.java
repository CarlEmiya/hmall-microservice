package com.hmall.trade.listener;

import com.hmall.api.client.PayClient;

import com.hmall.api.domain.dto.PayOrderDTO;

import com.hmall.api.domain.po.Order;
import com.hmall.common.constants.MqConstants;
import com.hmall.trade.service.IOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderDelayMessageListener {

    private final IOrderService orderService;
    private final PayClient payClient;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = MqConstants.DELAY_ORDER_QUEUE_NAME),
            exchange = @Exchange(name = MqConstants.DELAY_EXCHANGE_NAME, delayed = "true"),
            key = MqConstants.DELAY_ORDER_KEY
    ))
    public void listenOrderDelayMessage(Long orderId){
        System.out.println("\n=== [OrderDelayMessageListener] 接收到延迟消息 ===");
        System.out.println("[OrderDelayMessageListener] 队列: " + MqConstants.DELAY_ORDER_QUEUE_NAME);
        System.out.println("[OrderDelayMessageListener] 交换机: " + MqConstants.DELAY_EXCHANGE_NAME + " (延迟交换机)");
        System.out.println("[OrderDelayMessageListener] 路由键: " + MqConstants.DELAY_ORDER_KEY);
        System.out.println("[OrderDelayMessageListener] 接收时间: " + new java.util.Date());
        System.out.println("[OrderDelayMessageListener] 订单ID: " + orderId);
        
        // 1.查询订单
        System.out.println("[OrderDelayMessageListener] 步骤1: 查询订单信息...");
        Order order = orderService.getById(orderId);
        
        // 2.检测订单状态，判断是否已支付
        if(order == null){
            System.out.println("[OrderDelayMessageListener] ⚠️ 订单不存在，订单ID: " + orderId);
            return;
        }
        
        System.out.println("[OrderDelayMessageListener] 订单信息:");
        System.out.println("[OrderDelayMessageListener] - 订单状态: " + order.getStatus() + " (1=未支付, 2=已支付, 3=已发货, 4=已完成, 5=已取消)");
        System.out.println("[OrderDelayMessageListener] - 订单金额: " + order.getTotalFee() + "分");
        System.out.println("[OrderDelayMessageListener] - 用户ID: " + order.getUserId());
        
        if(order.getStatus() != 1){
            System.out.println("[OrderDelayMessageListener] ✅ 订单已支付或已处理，无需取消");
            return;
        }
        
        // 3.未支付，需要查询支付流水状态
        System.out.println("[OrderDelayMessageListener] 步骤2: 订单未支付，查询支付流水状态...");
        PayOrderDTO payOrder = payClient.queryPayOrderByBizOrderNo(orderId);
        
        // 4.判断是否支付
        if(payOrder != null){
            System.out.println("[OrderDelayMessageListener] 支付流水信息:");
            System.out.println("[OrderDelayMessageListener] - 支付状态: " + payOrder.getStatus() + " (1=待支付, 2=支付中, 3=支付成功, 4=支付失败)");
            System.out.println("[OrderDelayMessageListener] - 支付金额: " + payOrder.getAmount() + "分");
        } else {
            System.out.println("[OrderDelayMessageListener] 未找到支付流水记录");
        }
        
        if(payOrder != null && payOrder.getStatus() == 3){
            // 4.1.已支付，标记订单状态为已支付
            System.out.println("[OrderDelayMessageListener] ✅ 支付成功，更新订单状态为已支付");
            orderService.markOrderPaySuccess(orderId);
            System.out.println("[OrderDelayMessageListener] 订单支付状态更新完成");
        }else{
            // 4.2.未支付，取消订单，回复库存
            System.out.println("[OrderDelayMessageListener] ❌ 订单未支付，执行订单取消操作");
            orderService.cancelOrder(orderId);
            System.out.println("[OrderDelayMessageListener] 订单取消完成，库存已恢复");
        }
        System.out.println("[OrderDelayMessageListener] 延迟消息处理完成\n");
    }
}