package com.hmall.api.client;


import com.hmall.api.client.fallback.ItemClientFallback;
import com.hmall.api.config.DefaultFeignConfig;

import com.hmall.api.domain.dto.ItemDTO;
import com.hmall.api.domain.dto.OrderDetailDTO;
import com.hmall.api.domain.query.ItemPageQuery;
import com.hmall.common.domain.PageDTO;
import com.hmall.common.domain.PageQuery;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;


@FeignClient(value = "item-service", configuration = DefaultFeignConfig.class, fallbackFactory = ItemClientFallback.class)
public interface ItemClient {

    @GetMapping("/items")
    List<ItemDTO> queryItemByIds(@RequestParam("ids") Collection<Long> ids);

    @PutMapping("/items/stock/deduct")
    void deductStock(@RequestBody List<OrderDetailDTO> items);

    @ApiOperation("搜索商品")
    @GetMapping("/search/list")
    PageDTO<ItemDTO> search(ItemPageQuery query);

//    @ApiOperation("分页查询商品")
//    @GetMapping("/items/page")
//    PageDTO<ItemDTO> queryItemByPage(PageQuery query);
//
//    @ApiOperation("根据id查询商品")
//    @GetMapping("/items/{id}")
//    ItemDTO queryItemById(@PathVariable("id") Long id);
//
//    @ApiOperation("新增商品")
//    @PostMapping("/items")
//    void saveItem(@RequestBody ItemDTO item);
//
//    @ApiOperation("更新商品状态")
//    @PutMapping("/items/status/{id}/{status}")
//    void updateItemStatus(@PathVariable("id") Long id, @PathVariable("status") Integer status);
//
//    @ApiOperation("更新商品")
//    @PutMapping("/items")
//    void updateItem(@RequestBody ItemDTO item);
//
//    @ApiOperation("根据id删除商品")
//    @DeleteMapping("/items/{id}")
//    void deleteItemById(@PathVariable("id") Long id);
}
