package com.hmall.common.utils;

import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.nio.charset.Charset;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 布隆过滤器工具类
 * 用于防止缓存穿透，快速判断数据是否可能存在
 */
@Slf4j
@Component
public class BloomFilterUtils {

    private final ConcurrentHashMap<String, BloomFilter<String>> bloomFilters = new ConcurrentHashMap<>();

    /**
     * 创建布隆过滤器
     * @param filterName 过滤器名称
     * @param expectedInsertions 预期插入数量
     * @param fpp 误判率（false positive probability）
     */
    public void createBloomFilter(String filterName, int expectedInsertions, double fpp) {
        BloomFilter<String> bloomFilter = BloomFilter.create(
                Funnels.stringFunnel(Charset.defaultCharset()),
                expectedInsertions,
                fpp
        );
        bloomFilters.put(filterName, bloomFilter);
        log.info("创建布隆过滤器: {}, 预期插入数量: {}, 误判率: {}", filterName, expectedInsertions, fpp);
    }

    /**
     * 向布隆过滤器添加元素
     */
    public void put(String filterName, String element) {
        BloomFilter<String> bloomFilter = bloomFilters.get(filterName);
        if (bloomFilter != null) {
            bloomFilter.put(element);
            log.debug("向布隆过滤器 {} 添加元素: {}", filterName, element);
        } else {
            log.warn("布隆过滤器 {} 不存在", filterName);
        }
    }

    /**
     * 判断元素是否可能存在
     * @param filterName 过滤器名称
     * @param element 要检查的元素
     * @return true表示可能存在，false表示一定不存在
     */
    public boolean mightContain(String filterName, String element) {
        BloomFilter<String> bloomFilter = bloomFilters.get(filterName);
        if (bloomFilter != null) {
            boolean result = bloomFilter.mightContain(element);
            log.debug("布隆过滤器 {} 检查元素 {}: {}", filterName, element, result ? "可能存在" : "一定不存在");
            return result;
        } else {
            log.warn("布隆过滤器 {} 不存在，默认返回true", filterName);
            return true; // 如果过滤器不存在，默认允许通过
        }
    }

    /**
     * 获取布隆过滤器的预期误判率
     */
    public double getExpectedFpp(String filterName) {
        BloomFilter<String> bloomFilter = bloomFilters.get(filterName);
        if (bloomFilter != null) {
            return bloomFilter.expectedFpp();
        }
        return 0.0;
    }

    /**
     * 获取布隆过滤器中已插入的元素数量（近似值）
     */
    public long getApproximateElementCount(String filterName) {
        BloomFilter<String> bloomFilter = bloomFilters.get(filterName);
        if (bloomFilter != null) {
            return bloomFilter.approximateElementCount();
        }
        return 0L;
    }

    /**
     * 检查布隆过滤器是否存在
     */
    public boolean exists(String filterName) {
        return bloomFilters.containsKey(filterName);
    }

    /**
     * 移除布隆过滤器
     */
    public void removeBloomFilter(String filterName) {
        bloomFilters.remove(filterName);
        log.info("移除布隆过滤器: {}", filterName);
    }

    /**
     * 获取所有布隆过滤器的名称
     */
    public java.util.Set<String> getAllFilterNames() {
        return bloomFilters.keySet();
    }

    @PostConstruct
    public void init() {
        log.info("布隆过滤器工具类初始化完成");
    }

    // 常用的布隆过滤器名称常量
    public static final String ITEM_FILTER = "item_filter";
    public static final String USER_FILTER = "user_filter";
    public static final String CATEGORY_FILTER = "category_filter";
}