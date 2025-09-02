package com.hmall.api.client;

import com.hmall.api.client.fallback.ItemClientFallback;
import com.hmall.api.config.DefaultFeignConfig;
import com.hmall.api.domain.dto.OrderFormDTO;
import com.hmall.api.domain.dto.PayApplyDTO;
import com.hmall.api.domain.dto.PayOrderFormDTO;
import com.hmall.api.domain.vo.OrderVO;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collection;
import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import java.util.Collection;
import java.util.List;

@FeignClient(value = "trade-service", configuration = DefaultFeignConfig.class, fallbackFactory = ItemClientFallback.class)
public interface TradeClient {

    /**
     * 订单相关接口
     */
    @ApiOperation("根据id查询订单")
    @GetMapping("/orders/{id}")
    OrderVO queryOrderById(@PathVariable("id") Long orderId);

    @ApiOperation("创建订单")
    @PostMapping("/orders")
    Long createOrder(@RequestBody OrderFormDTO orderFormDTO);

    @ApiOperation("标记订单已支付")
    @ApiImplicitParam(name = "orderId", value = "订单id", paramType = "path")
    @PutMapping("/orders/{orderId}")
    void markOrderPaySuccess(@PathVariable("orderId") Long orderId);

    /**
     * 支付相关接口
     */
    @ApiOperation("生成支付单")
    @PostMapping("/pay-orders")
    String applyPayOrder(@RequestBody PayApplyDTO applyDTO);

    @ApiOperation("尝试基于用户余额支付")
    @ApiImplicitParam(value = "支付单id", name = "id")
    @PostMapping("/pay-orders/{id}")
    void tryPayOrderByBalance(@PathVariable("id") Long id, @RequestBody PayOrderFormDTO payOrderFormDTO);
}
