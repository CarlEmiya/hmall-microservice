package com.hmall.api.client.fallback;

import com.hmall.api.client.TradeClient;
import com.hmall.api.domain.dto.OrderFormDTO;
import com.hmall.api.domain.dto.PayApplyDTO;
import com.hmall.api.domain.dto.PayOrderFormDTO;
import com.hmall.api.domain.vo.OrderVO;

import com.hmall.common.exception.BizIllegalException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;

@Slf4j
public class TradeClientFallback implements FallbackFactory<TradeClient> {
    @Override
    public TradeClient create(Throwable cause) {
        return new TradeClient() {
            @Override
            public OrderVO queryOrderById(Long orderId) {
                log.error("远程调用TradeClient#queryOrderById方法出现异常，参数：{}", orderId, cause);
                // 查询订单允许失败，返回null
                return null;
            }

            @Override
            public Long createOrder(OrderFormDTO orderFormDTO) {
                log.error("远程调用TradeClient#createOrder方法出现异常，参数：{}", orderFormDTO, cause);
                // 创建订单业务需要触发事务回滚，抛出异常
                throw new BizIllegalException("订单服务暂时不可用，创建订单失败: " + cause.getMessage());
            }

            @Override
            public Boolean markOrderPaySuccess(Long orderId) {
                log.error("远程调用TradeClient#markOrderPaySuccess方法出现异常，参数：{}", orderId, cause);
                // 标记订单支付成功业务需要触发事务回滚，抛出异常
                throw new BizIllegalException("订单服务暂时不可用，更新订单状态失败: " + cause.getMessage());
            }

            @Override
            public Boolean cancelOrder(Long orderId) {
                log.error("取消订单失败，远程调用TradeClient#cancelOrder方法出现异常，参数：{}", orderId, cause);
                throw new BizIllegalException("订单服务暂时不可用，取消订单失败: " + cause.getMessage());
            }

        };
    }
}