package com.hmall.trade.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hmall.api.client.CartClient;
import com.hmall.api.client.ItemClient;
import com.hmall.common.utils.CacheUtils;
import com.hmall.common.constants.CacheConstants;


import com.hmall.api.client.UserClient;
import com.hmall.api.domain.dto.ItemDTO;
import com.hmall.api.domain.dto.OrderDetailDTO;
import com.hmall.api.domain.dto.OrderFormDTO;
import com.hmall.api.domain.po.Order;
import com.hmall.api.domain.po.OrderDetail;
import com.hmall.common.constants.MqConstants;
import com.hmall.common.exception.BadRequestException;
import com.hmall.common.exception.BizIllegalException;
import com.hmall.common.utils.UserContext;

import com.hmall.api.domain.po.TradeMessage;
import com.hmall.trade.mapper.OrderMapper;
import com.hmall.trade.service.IOrderDetailService;
import com.hmall.trade.service.IOrderService;
import io.seata.spring.annotation.GlobalTransactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.hmall.common.utils.RabbitMqHelper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author 虎哥
 * @since 2023-05-05
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements IOrderService {

    private final ItemClient itemClient;
    private final IOrderDetailService detailService;
    private final CartClient cartClient;
    private final UserClient userClient;
    private final RabbitMqHelper rabbitMqHelper;
    private final RabbitTemplate rabbitTemplate;
    private final CacheUtils cacheUtils;

    @Override
    @GlobalTransactional
    public Long createOrder(OrderFormDTO orderFormDTO) {
        // 1.订单数据
        Order order = new Order();
        // 1.1.查询商品
        List<OrderDetailDTO> detailDTOS = orderFormDTO.getDetails();
        // 1.2.获取商品id和数量的Map
        Map<Long, Integer> itemNumMap = detailDTOS.stream()
                .collect(Collectors.toMap(OrderDetailDTO::getItemId,OrderDetailDTO::getNum));
        Set<Long> itemIds = itemNumMap.keySet();
        // 1.3.查询商品
        List<ItemDTO> items = itemClient.queryItemByIds(itemIds);
        if (items == null || items.size() < itemIds.size()) {
            throw new BadRequestException("商品不存在");
        }
        // 1.4.基于商品价格、购买数量计算商品总价：totalFee
        int total = 0;
        for (ItemDTO item : items) {
            total += item.getPrice() * itemNumMap.get(item.getId());
        }
        order.setTotalFee(total);
        // 1.5.其它属性
        order.setPaymentType(orderFormDTO.getPaymentType());
        order.setUserId(UserContext.getUser());
        order.setStatus(1);
        // 1.6.将Order写入数据库order表中
        save(order);

        // 2.保存订单详情
        List<OrderDetail> details = buildDetails(order.getId(), items, itemNumMap);
        detailService.saveBatch(details);

        // 3.清理购物车商品
//        cartClient.deleteCartItemByIds(itemIds);
//        发送一条消息到trade.topic，发送消息的RoutingKey  为order.create，消息内容是下单的具体商品、当前登录用户信息

        TradeMessage tradeMessage = new TradeMessage();
        tradeMessage.setOrder(order);
        tradeMessage.setOrderDetail(details);
        tradeMessage.setUserId(UserContext.getUser());

        System.out.println("\n=== [交易服务] 订单创建成功，准备发送订单创建消息 ===");
        System.out.println("[交易服务] 订单ID: " + order.getId());
        System.out.println("[交易服务] 用户ID: " + UserContext.getUser());
        System.out.println("[交易服务] 订单总金额: " + order.getTotalFee() + "分");
        System.out.println("[交易服务] 订单商品数量: " + details.size() + "种");
        System.out.println("[交易服务] 即将发送消息到 trade.topic 交换机，路由键: order.create");
        // 使用带确认机制的消息发送
        rabbitMqHelper.sendMessageWithConfirm("trade.topic", "order.create", tradeMessage, 3);
        System.out.println("[交易服务] 订单创建消息发送完成\n");


        // 4.扣减库存
        try {
            List<OrderDetailDTO> apiDetailDTOS = detailDTOS.stream()
                    .map(dto -> {
                        OrderDetailDTO apiDto = new OrderDetailDTO();
                        apiDto.setItemId(dto.getItemId());
                        apiDto.setNum(dto.getNum());
                        return apiDto;
                    })
                    .collect(Collectors.toList());
            itemClient.deductStock(apiDetailDTOS);
        } catch (Exception e) {
            throw new RuntimeException("库存不足！");
        }

        //5.发送延迟消息，30分钟后，如果订单未支付成功，则自动取消订单
        System.out.println("\n=== [交易服务] 准备发送订单延迟取消消息 ===");
        System.out.println("[交易服务] 订单ID: " + order.getId());
        System.out.println("[交易服务] 延迟时间: 1000ms (测试用，实际应为30分钟)");
        System.out.println("[交易服务] 延迟交换机: " + MqConstants.DELAY_EXCHANGE_NAME);
        System.out.println("[交易服务] 延迟路由键: " + MqConstants.DELAY_ORDER_KEY);
        rabbitTemplate.convertAndSend(MqConstants.DELAY_EXCHANGE_NAME,
                MqConstants.DELAY_ORDER_KEY,
                order.getId(),
                message -> {message.getMessageProperties().setDelay(30*60*1000); return message;}
        );
        System.out.println("[交易服务] 延迟取消消息发送完成，订单将在1秒后自动检查支付状态\n");

        System.out.println("成功发送延迟消息，10秒后，如果订单未支付成功，则自动取消订单");

        return order.getId();
    }

    @Override
    public Boolean markOrderPaySuccess(Long orderId) {
        log.info("标记订单支付成功，订单ID: {}", orderId);
        
        // 1.查询订单
        Order old = getById(orderId);
        // 2.判断订单状态
        if (old == null || old.getStatus() != 1) {
            // 订单不存在或者订单状态不是1，放弃处理
            log.warn("订单不存在或状态不正确，订单ID: {}", orderId);
            return false;
        }
        // 3.尝试更新订单
        Order order = new Order();
        order.setId(orderId);
        order.setStatus(2);
        order.setPayTime(LocalDateTime.now());
        boolean result = updateById(order);
        
        if (result) {
            // 清除订单缓存
            clearOrderCache(orderId);
            log.info("订单支付成功，已清除缓存，订单ID: {}", orderId);
        }
        
        return result;
    }

    @Override
    public Boolean cancelOrder(Long orderId) {
        log.info("取消订单，订单ID: {}", orderId);
        
        // 1.查询订单
        Order old = getById(orderId);
        // 乐观锁取消订单
        if (old == null || (old.getStatus() != 1 && old.getStatus() != 2)) {
            // 订单不存在或者订单状态不是1或2，放弃处理
            log.warn("订单不存在或状态不正确，订单ID: {}", orderId);
            return null;
        }
        // 2.尝试更新订单
        Order order = new Order();
        order.setId(orderId);
        order.setStatus(5);
        order.setCloseTime(LocalDateTime.now());
        order.setEndTime(LocalDateTime.now());
        order.setUpdateTime(LocalDateTime.now());

        Boolean res = updateById(order);
        if (res) {
            // 清除订单缓存
            clearOrderCache(orderId);
            log.info("订单取消成功，已清除缓存，订单ID: {}", orderId);
            
            // 3.回滚库存
            List<OrderDetail> details = detailService.list(new QueryWrapper<OrderDetail>().eq("order_id", orderId));
            List<OrderDetailDTO> detailDTOS = details.stream()
                    .map(detail -> {
                        OrderDetailDTO dto = new OrderDetailDTO();
                        dto.setItemId(detail.getItemId());
                        dto.setNum(detail.getNum());
                        return dto;
                    })
                    .collect(Collectors.toList());
            try {
                itemClient.addStock(detailDTOS);
                log.info("库存回滚成功，订单ID: {}", orderId);
            } catch (Exception e) {
                log.error("库存回滚失败，订单ID: {}", orderId, e);
                throw new BizIllegalException("库存回滚失败！");
            }
            return true;
        }else{
            log.warn("订单取消失败，订单ID: {}", orderId);
            return false;
        }
    }

    private List<OrderDetail> buildDetails(Long orderId, List<ItemDTO> items, Map<Long, Integer> numMap) {
        List<OrderDetail> details = new ArrayList<>(items.size());
        for (ItemDTO item : items) {
            OrderDetail detail = new OrderDetail();
            detail.setName(item.getName());
            detail.setSpec(item.getSpec());
            detail.setPrice(item.getPrice());
            detail.setNum(numMap.get(item.getId()));
            detail.setItemId(item.getId());
            detail.setImage(item.getImage());
            detail.setOrderId(orderId);
            details.add(detail);
        }
        return details;
    }

    /**
     * 重写updateById方法，添加缓存清理逻辑
     */
    @Override
    public boolean updateById(Order entity) {
        log.info("更新订单，订单ID: {}", entity.getId());
        boolean result = super.updateById(entity);
        if (result && entity.getId() != null) {
            clearOrderCache(entity.getId());
            log.info("订单更新成功，已清除缓存，订单ID: {}", entity.getId());
        }
        return result;
    }

    /**
     * 清除订单缓存
     */
    private void clearOrderCache(Long orderId) {
        String cacheKey = CacheConstants.ORDER_CACHE_KEY_PREFIX + orderId;
        cacheUtils.deleteByKey(cacheKey);
        log.debug("清除订单缓存，缓存键: {}", cacheKey);
    }
}