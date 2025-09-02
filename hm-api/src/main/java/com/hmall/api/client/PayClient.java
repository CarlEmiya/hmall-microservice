package com.hmall.api.client;

import com.hmall.api.client.fallback.ItemClientFallback;
import com.hmall.api.config.DefaultFeignConfig;
import com.hmall.api.domain.dto.PayApplyDTO;
import com.hmall.api.domain.dto.PayOrderFormDTO;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(value = "pay-service", configuration = DefaultFeignConfig.class, fallbackFactory = ItemClientFallback.class)
public interface PayClient {

    /**
     * 生成支付单
     * @param applyDTO 支付申请数据传输对象
     * @return 支付单相关信息
     */
    @ApiOperation("生成支付单")
    @PostMapping("/pay-orders")
    String applyPayOrder(@RequestBody PayApplyDTO applyDTO);

    /**
     * 尝试基于用户余额支付
     * @param id 支付单id
     * @param payOrderFormDTO 支付单表单数据传输对象
     */
    @ApiOperation("尝试基于用户余额支付")
    @ApiImplicitParam(value = "支付单id", name = "id")
    @PostMapping("/pay-orders/{id}")
    void tryPayOrderByBalance(@PathVariable("id") Long id, @RequestBody PayOrderFormDTO payOrderFormDTO);
}
