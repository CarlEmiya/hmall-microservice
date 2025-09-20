package com.hmall.item.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hmall.item.domain.query.ItemPageQuery;
import com.hmall.common.domain.PageDTO;
import com.hmall.common.utils.BeanUtils;
import com.hmall.common.utils.CacheConstants;
import com.hmall.common.utils.CacheKeyUtils;
import com.hmall.common.utils.CacheUtils;
import com.hmall.item.domain.dto.ItemDTO;
import com.hmall.item.domain.dto.OrderDetailDTO;
import com.hmall.item.domain.po.Item;
import com.hmall.item.service.IItemService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "商品管理相关接口")
@Slf4j
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final IItemService itemService;
    private final RabbitTemplate rabbitTemplate;
    private final CacheUtils cacheUtils;

    @ApiOperation("分页查询商品")
    @GetMapping("/page")
    public PageDTO<Item> queryItemByPage(ItemPageQuery query) {
        log.info("分页查询商品，参数: {}", query);
        
        // 构建缓存键
        String cacheKey = CacheKeyUtils.buildItemListKey(query.getKey(), query.getBrand(), query.getCategory(), query.getMinPrice(), query.getMaxPrice());
        
        // 使用Cache Aside模式查询
        return cacheUtils.queryWithCacheAside(
                cacheKey,
                PageDTO.class,
                () -> {
                    log.info("从数据库查询商品分页数据");
                    // 分页查询
                    Page<Item> result = itemService.lambdaQuery()
                            .like(query.getKey() != null, Item::getName, query.getKey())
                            .eq(query.getBrand() != null, Item::getStatus, query.getBrand())
                            .eq(query.getCategory() != null, Item::getCategory, query.getCategory())
                            .gt(query.getMinPrice() != null, Item::getPrice, query.getMinPrice())
                            .lt(query.getMaxPrice() != null, Item::getPrice, query.getMaxPrice())
                            .page(query.toMpPage("update_time", false));
                    // 封装并返回
                    return PageDTO.of(result, Item.class);
                },
                CacheConstants.ITEM_LIST_TTL
        );
    }

    @ApiOperation("根据id批量查询商品")
    @GetMapping
    public List<ItemDTO> queryItemByIds(@RequestParam("ids") List<Long> ids){
        return itemService.queryItemByIds(ids);
    }

    @ApiOperation("根据id查询商品")
    @GetMapping("{id}")
    public ItemDTO queryItemById(@PathVariable("id") Long id) {
        log.debug("查询商品详情: {}", id);
        
        // 使用Cache Aside模式查询商品
        return cacheUtils.queryWithCacheAside(
                CacheConstants.ITEM_CACHE_KEY_PREFIX,
                id,
                ItemDTO.class,
                itemId -> {
                    Item item = itemService.getById(itemId);
                    return item != null ? BeanUtils.copyBean(item, ItemDTO.class) : null;
                },
                CacheConstants.ITEM_CACHE_TTL,
                CacheConstants.ITEM_CACHE_TTL_UNIT
        );
    }

    @ApiOperation("新增商品")
    @PostMapping
    public void saveItem(@RequestBody ItemDTO item) {
        // 新增
        Item savedItem = BeanUtils.copyBean(item, Item.class);
        itemService.save(savedItem);
        
        // 清除相关缓存
        cacheUtils.deleteByPattern(CacheConstants.ITEM_CACHE_KEY_PREFIX + "*");
        cacheUtils.deleteByPattern(CacheConstants.ITEM_LIST_KEY_PREFIX + "*");
        
        // 发送MQ消息通知搜索服务同步数据
        rabbitTemplate.convertAndSend("item.topic", "item.insert", savedItem.getId());
        log.info("新增商品成功，ID: {}", savedItem.getId());
    }

    @ApiOperation("更新商品状态")
    @PutMapping("/status/{id}/{status}")
    public void updateItemStatus(@PathVariable("id") Long id, @PathVariable("status") Integer status){
        Item item = new Item();
        item.setId(id);
        item.setStatus(status);
        itemService.updateById(item);
        
        // 删除相关缓存
        String itemKey = CacheConstants.buildItemKey(id);
        cacheUtils.deleteBatch(itemKey);
        cacheUtils.deleteByPattern(CacheConstants.ITEM_LIST_KEY_PREFIX + "*");
        
        // 发送MQ消息通知搜索服务同步数据
        rabbitTemplate.convertAndSend("item.topic", "item.update", id);
        log.info("更新商品状态成功，ID: {}, 状态: {}", id, status);
    }

    @ApiOperation("更新商品")
    @PutMapping
    public void updateItem(@RequestBody ItemDTO item) {
        // 不允许修改商品状态，所以强制设置为null，更新时，就会忽略该字段
        item.setStatus(null);
        // 更新
        itemService.updateById(BeanUtils.copyBean(item, Item.class));
        
        // 删除相关缓存
        String itemKey = CacheConstants.buildItemKey(item.getId());
        cacheUtils.deleteBatch(itemKey);
        cacheUtils.deleteByPattern(CacheConstants.ITEM_LIST_KEY_PREFIX + "*");
        
        // 发送MQ消息通知搜索服务同步数据
        rabbitTemplate.convertAndSend("item.topic", "item.update", item.getId());
        log.info("更新商品成功，ID: {}", item.getId());
    }

    @ApiOperation("根据id删除商品")
    @DeleteMapping("{id}")
    public void deleteItemById(@PathVariable("id") Long id) {
        itemService.removeById(id);
        
        // 删除相关缓存
        String itemKey = CacheConstants.buildItemKey(id);
        String stockKey = CacheConstants.buildItemStockKey(id);
        cacheUtils.deleteBatch(itemKey, stockKey);
        cacheUtils.deleteByPattern(CacheConstants.ITEM_LIST_KEY_PREFIX + "*");
        
        // 发送MQ消息通知搜索服务
        System.out.println("[ItemController] === 商品删除成功，发送MQ消息 ===");
        System.out.println("  - 商品ID: " + id);
        
        try {
            rabbitTemplate.convertAndSend("item.topic", "item.delete", id);
            System.out.println("[ItemController] ✅ MQ消息发送成功 - item.delete");
            log.info("删除商品成功，ID: {}", id);
        } catch (Exception e) {
            System.out.println("[ItemController] ❌ MQ消息发送失败: " + e.getMessage());
            log.error("删除商品MQ消息发送失败，ID: {}", id, e);
        }
    }

    @ApiOperation("批量扣减库存")
    @PutMapping("/stock/deduct")
    public void deductStock(@RequestBody List<OrderDetailDTO> items){
        itemService.deductStock(items);
    }

    //增加库存，void addStock(List<OrderDetailDTO> detailDTOS);
    @PostMapping("/stock/add")
    public void addStock(@RequestBody List<OrderDetailDTO> detailDTOS){
        itemService.addStock(detailDTOS);
    }
}
