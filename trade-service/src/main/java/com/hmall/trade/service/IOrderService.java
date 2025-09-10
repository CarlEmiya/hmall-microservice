package com.hmall.trade.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.hmall.api.domain.dto.OrderFormDTO;
import com.hmall.api.domain.po.Order;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 虎哥
 * @since 2023-05-05
 */
public interface IOrderService extends IService<Order> {

    Long createOrder(OrderFormDTO orderFormDTO);

    Boolean markOrderPaySuccess(Long orderId);

    Boolean cancelOrder(Long orderId);
}