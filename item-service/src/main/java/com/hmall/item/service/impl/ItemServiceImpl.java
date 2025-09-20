package com.hmall.item.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hmall.common.exception.BizIllegalException;
import com.hmall.common.utils.BeanUtils;
import com.hmall.common.utils.CacheUtils;
import com.hmall.common.utils.CacheConstants;
import com.hmall.common.utils.BloomFilterUtils;
import com.hmall.item.domain.dto.ItemDTO;
import com.hmall.item.domain.dto.OrderDetailDTO;
import com.hmall.item.domain.po.Item;
import com.hmall.item.mapper.ItemMapper;
import com.hmall.item.service.IItemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 商品表 服务实现类
 * </p>
 *
 * @author 虎哥
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl extends ServiceImpl<ItemMapper, Item> implements IItemService {

    private final RabbitTemplate rabbitTemplate;
    private final CacheUtils cacheUtils;
    private final BloomFilterUtils bloomFilterUtils;

    @Override
    public void deductStock(List<OrderDetailDTO> items) {
        log.info("开始扣减库存，商品数量: {}", items.size());
        
        // 使用延迟双删策略处理库存扣减
        String[] stockKeys = items.stream()
                .map(item -> CacheConstants.buildItemStockKey(item.getItemId()))
                .toArray(String[]::new);
        
        cacheUtils.delayedDoubleDelete(
                String.join(",", stockKeys),
                () -> {
                    String sqlStatement = "com.hmall.item.mapper.ItemMapper.updateStock";
                    boolean r = false;
                    try {
                        r = executeBatch(items, (sqlSession, entity) -> sqlSession.update(sqlStatement, entity));
                    } catch (Exception e) {
                        log.error("批量扣减库存异常", e);
                        throw new BizIllegalException("更新库存异常，可能是库存不足!", e);
                    }
                    if (!r) {
                        log.warn("库存扣减失败，可能库存不足");
                        throw new BizIllegalException("库存不足！");
                    }
                    log.info("库存扣减成功");
                },
                CacheConstants.STOCK_DELAY_DELETE_TIME
        );
        
        // 同时删除相关商品缓存
        String[] itemKeys = items.stream()
                .map(item -> CacheConstants.buildItemKey(item.getItemId()))
                .toArray(String[]::new);
        cacheUtils.deleteBatch(itemKeys);
    }

    @Override
    public List<ItemDTO> queryItemByIds(Collection<Long> ids) {
        log.debug("批量查询商品，ID数量: {}", ids.size()+"\n其中id有："+ ids);
        
        // 使用Cache Aside模式查询商品信息
        return ids.stream()
                .map(this::queryItemByIdWithCache)
                .collect(Collectors.toList());
    }
    
    /**
     * 使用缓存查询单个商品
     */
    private ItemDTO queryItemByIdWithCache(Long id) {
        // 先检查布隆过滤器
        if (!bloomFilterUtils.mightContain(BloomFilterUtils.ITEM_FILTER, String.valueOf(id))) {
            log.debug("布隆过滤器判断商品不存在: {}", id);

            return null;
        }
        
        // 使用Cache Aside模式查询
        return cacheUtils.queryWithCacheAside(
                CacheConstants.ITEM_CACHE_KEY_PREFIX,
                id,
                ItemDTO.class,
                this::queryItemFromDb,
                CacheConstants.ITEM_CACHE_TTL,
                CacheConstants.ITEM_CACHE_TTL_UNIT
        );
    }
    
    /**
     * 从数据库查询商品并转换为DTO
     */
    private ItemDTO queryItemFromDb(Long id) {
        log.debug("从数据库查询商品: {}", id);
        Item item = getById(id);
        if (item != null) {
            // 将商品ID添加到布隆过滤器
            bloomFilterUtils.put(BloomFilterUtils.ITEM_FILTER, String.valueOf(id));
            return BeanUtils.copyBean(item, ItemDTO.class);
        }
        return null;
    }

    @Override
    public void addStock(List<OrderDetailDTO> detailDTOS) {
        log.info("开始增加库存，商品数量: {}", detailDTOS.size());
        
        // 使用延迟双删策略处理库存增加
        String[] stockKeys = detailDTOS.stream()
                .map(item -> CacheConstants.buildItemStockKey(item.getItemId()))
                .toArray(String[]::new);
        
        cacheUtils.delayedDoubleDelete(
                String.join(",", stockKeys),
                () -> {
                    // 批量语句，根据商品id和数量追加库存
                    String sqlStatement = "com.hmall.item.mapper.ItemMapper.addStock";
                    boolean r = false;
                    try {
                        // 执行批量增加库存操作
                        r = executeBatch(detailDTOS, (sqlSession, entity) -> sqlSession.update(sqlStatement, entity));
                    } catch (Exception e) {
                        log.error("批量增加库存异常", e);
                        // 增加库存异常，无需提示库存不足（与减少库存的区别）
                        throw new BizIllegalException("增加库存异常!", e);
                    }
                    if (!r) {
                        log.warn("增加库存失败");
                        // 执行失败时，明确提示增加库存失败（而非库存不足）
                        throw new BizIllegalException("增加库存失败！");
                    }
                    log.info("库存增加成功");
                },
                CacheConstants.STOCK_DELAY_DELETE_TIME
        );
        
        // 同时删除相关商品缓存
        String[] itemKeys = detailDTOS.stream()
                .map(item -> CacheConstants.buildItemKey(item.getItemId()))
                .toArray(String[]::new);
        cacheUtils.deleteBatch(itemKeys);
    }
}
