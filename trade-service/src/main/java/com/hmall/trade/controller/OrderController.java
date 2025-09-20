package com.hmall.trade.controller;

import com.hmall.api.domain.dto.OrderFormDTO;
import com.hmall.api.domain.vo.OrderVO;
import com.hmall.common.utils.BeanUtils;
import com.hmall.common.utils.CacheUtils;
import com.hmall.common.constants.CacheConstants;

import com.hmall.trade.service.IOrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Param;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.TimeUnit;

@Api(tags = "订单管理接口")
@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
@Slf4j
public class OrderController {
    private final IOrderService orderService;
    private final CacheUtils cacheUtils;

    @ApiOperation("根据id查询订单")
    @GetMapping("{id}")
    public OrderVO queryOrderById(@PathVariable("id") Long orderId) {
        log.info("查询订单，订单ID: {}", orderId);

        // 调用CacheUtils的queryWithCacheAside方法，严格匹配参数顺序和类型
        OrderVO orderVO = cacheUtils.queryWithCacheAside(
                // 1. 缓存键前缀（无需拼接ID，方法内部自动拼接）
                CacheConstants.ORDER_CACHE_KEY_PREFIX,
                // 2. 业务ID（订单ID）
                orderId,
                // 3. 反序列化目标类型
                OrderVO.class,
                // 4. 数据库查询逻辑（Function<Long, OrderVO>：接收Long型ID，返回OrderVO）
                (Long id) -> {  // 此处id为Function的入参，与订单ID一致
                    log.info("从数据库查询订单，订单ID: {}", id);
                    // 1. 查数据库（用Function入参id，避免外部变量依赖）
                    Object orderDb = orderService.getById(id);
                    // 2. 复制为OrderVO（确保BeanUtils.copyBean支持“源对象→目标类型”的转换）
                    return BeanUtils.copyBean(orderDb, OrderVO.class);
                },
                // 5. 缓存过期时间（从常量类获取，Long类型）
                CacheConstants.ORDER_CACHE_TTL,
                // 6. 时间单位（与ORDER_CACHE_TTL的单位一致，此处为秒）
                TimeUnit.SECONDS
        );

        log.info("订单查询完成，订单ID: {}", orderId);
        return orderVO;
    }

    @ApiOperation("创建订单")
    @PostMapping
    public Long createOrder(@RequestBody OrderFormDTO orderFormDTO){
        return orderService.createOrder(orderFormDTO);
    }

    @ApiOperation("标记订单已支付")
    @ApiImplicitParam(name = "orderId", value = "订单id", paramType = "path")
    @PutMapping("/{orderId}")
    public Boolean markOrderPaySuccess(@PathVariable("orderId") Long orderId) {
        Boolean result = orderService.markOrderPaySuccess(orderId);
        return result;
    }

    //取消订单
    @ApiOperation("取消订单")
    @ApiImplicitParam(name = "orderId", value = "订单id", paramType = "path")
    @PutMapping("/cancel/{orderId}")
    public Boolean cancelOrder(@PathVariable("orderId") Long orderId) {
        Boolean result = orderService.cancelOrder(orderId);
        System.out.println("取消订单 " + orderId + " 成功！");
        return result;
    }
}