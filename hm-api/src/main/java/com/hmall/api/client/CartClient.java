package com.hmall.api.client;

import com.hmall.api.client.fallback.ItemClientFallback;
import com.hmall.api.config.DefaultFeignConfig;
import com.hmall.api.domain.dto.CartFormDTO;
import com.hmall.api.domain.po.Cart;
import com.hmall.api.domain.vo.CartVO;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Collection;
import java.util.List;

@FeignClient(value = "cart-service", configuration = DefaultFeignConfig.class, fallbackFactory = ItemClientFallback.class)
public interface CartClient {

    /**
     * 添加商品到购物车
     * @param cartFormDTO 购物车表单数据传输对象
     */
    @ApiOperation("添加商品到购物车")
    @PostMapping("/carts")
    void addItem2Cart(@Valid @RequestBody CartFormDTO cartFormDTO);

    /**
     * 更新购物车数据
     * @param cart 购物车实体对象
     */
    @ApiOperation("更新购物车数据")
    @PutMapping("/carts")
    void updateCart(@RequestBody Cart cart);

    /**
     * 删除购物车中商品
     * @param id 购物车条目id
     */
    @ApiOperation("删除购物车中商品")
    @DeleteMapping("/carts/{id}")
    void deleteCartItem(@PathVariable("id") Long id);

    /**
     * 查询购物车列表
     * @return 购物车视图对象列表
     */
    @ApiOperation("查询购物车列表")
    @GetMapping("/carts")
    List<CartVO> queryMyCarts();

    /**
     * 批量删除购物车中商品
     * @param ids 购物车条目id集合
     */
    @ApiOperation("批量删除购物车中商品")
    @ApiImplicitParam(name = "ids", value = "购物车条目id集合")
    @DeleteMapping("/carts")
    void deleteCartItemByIds(@RequestParam("ids") Collection<Long> ids);
}