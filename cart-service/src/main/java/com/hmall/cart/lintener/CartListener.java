package com.hmall.cart.lintener;

import com.hmall.api.domain.po.OrderDetail;
import com.hmall.api.domain.po.TradeMessage;
import com.hmall.cart.service.ICartService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Slf4j
@RequiredArgsConstructor
public class CartListener {

    private final ICartService cartService;




//    - 购物车服务监听cart.clear.queue队列，接收到消息后清理指定用户的购物车中的指定商品
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "cart.clear.queue",durable = "true"),
            exchange = @Exchange(name = "trade.topic", type = ExchangeTypes.TOPIC),
            key = "order.create"
    ))
    public void listenTopicQueue12(TradeMessage msg){

//
//        // 添加这行代码模拟异常
//        if(msg.getUserId() != null) {
//            throw new RuntimeException("模拟消费异常");
//        }


        System.out.println("\n=== [CartListener] 接收到购物车清理消息 ===");
        System.out.println("[CartListener] 队列: cart.clear.queue");
        System.out.println("[CartListener] 交换机: trade.topic");
        System.out.println("[CartListener] 路由键: order.create");
        System.out.println("[CartListener] 接收时间: " + new java.util.Date());
        
        // 空值检查，防止空指针异常
        if (msg == null) {
            System.err.println("[CartListener] 错误: 接收到的消息为空，跳过处理");
            return;
        }

        // 获取用户id和商品id
        Long userId = msg.getUserId();
        List<Long> itemIds = msg.getOrderDetail().stream().map(OrderDetail::getItemId).collect(Collectors.toList());

        System.out.println("[CartListener] 消息内容解析:");
        System.out.println("[CartListener] - 用户ID: " + userId);
        System.out.println("[CartListener] - 需清理的商品ID列表: " + itemIds);
        System.out.println("[CartListener] - 商品数量: " + (itemIds != null ? itemIds.size() : 0));

        System.out.println("[CartListener] 开始执行购物车清理操作...");
        Boolean result = cartService.removeByItemIdsAndUserId(userId, itemIds);
        
        if (result != null && result) {
            System.out.println("[CartListener] ✅ 购物车清理成功");
        } else {
            System.out.println("[CartListener] ❌ 购物车清理失败或无需清理");
        }
        System.out.println("[CartListener] 购物车清理操作完成\n");
    }
}
