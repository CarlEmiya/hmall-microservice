package com.hmall.item.service;

import com.hmall.common.utils.BloomFilterUtils;
import com.hmall.item.mapper.ItemMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 布隆过滤器数据初始化服务
 * 在应用启动时加载所有商品ID到布隆过滤器
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BloomFilterInitService implements ApplicationRunner {

    private final ItemMapper itemMapper;
    private final BloomFilterUtils bloomFilterUtils;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("开始初始化商品布隆过滤器数据...");
        
        try {
            // 1. 从数据库查询所有商品ID
            List<Long> itemIds = itemMapper.selectAllIds();
            
            if (itemIds == null || itemIds.isEmpty()) {
                log.warn("数据库中没有商品数据，跳过布隆过滤器初始化");
                return;
            }
            
            // 2. 将所有商品ID添加到布隆过滤器
            for (Long itemId : itemIds) {
                bloomFilterUtils.put(BloomFilterUtils.ITEM_FILTER, String.valueOf(itemId));
            }
            
            // 3. 记录初始化结果
            long count = bloomFilterUtils.getApproximateElementCount(BloomFilterUtils.ITEM_FILTER);
            double fpp = bloomFilterUtils.getExpectedFpp(BloomFilterUtils.ITEM_FILTER);
            
            log.info("商品布隆过滤器初始化完成！");
            log.info("  - 加载商品数量: {}", itemIds.size());
            log.info("  - 过滤器中元素数量: {}", count);
            log.info("  - 预期误判率: {:.4f}", fpp);
            
        } catch (Exception e) {
            log.error("商品布隆过滤器初始化失败", e);
            throw e;
        }
    }
}