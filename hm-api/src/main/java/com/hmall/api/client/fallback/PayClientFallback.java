package com.hmall.api.client.fallback;

import com.hmall.api.client.PayClient;
import com.hmall.api.domain.dto.PayApplyDTO;
import com.hmall.api.domain.dto.PayOrderDTO;
import com.hmall.api.domain.dto.PayOrderFormDTO;

import com.hmall.common.exception.BizIllegalException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;

@Slf4j
public class PayClientFallback implements FallbackFactory<PayClient> {
    @Override
    public PayClient create(Throwable cause) {
        return new PayClient() {
            @Override
            public String applyPayOrder(PayApplyDTO applyDTO) {
                log.error("远程调用PayClient#applyPayOrder方法出现异常，参数：{}", applyDTO, cause);
                // 生成支付单业务需要触发事务回滚，抛出异常
                throw new BizIllegalException("支付服务暂时不可用，生成支付单失败: " + cause.getMessage());
            }

            @Override
            public void tryPayOrderByBalance(Long id, PayOrderFormDTO payOrderFormDTO) {
                log.error("远程调用PayClient#tryPayOrderByBalance方法出现异常，参数：id={}, payOrderFormDTO={}", id, payOrderFormDTO, cause);
                // 余额支付业务需要触发事务回滚，抛出异常
                throw new BizIllegalException("支付服务暂时不可用，余额支付失败: " + cause.getMessage());
            }

            @Override
            public PayOrderDTO queryPayOrderByBizOrderNo(Long id) {
                return null;
            }
        };
    }
}