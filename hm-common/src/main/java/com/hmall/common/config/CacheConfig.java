package com.hmall.common.config;

import com.hmall.common.utils.BloomFilterUtils;
import com.hmall.common.utils.CacheConstants;
import com.hmall.common.utils.CacheUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

/**
 * 缓存配置类
 */
@Slf4j
@Configuration
public class CacheConfig {

    @Resource
    private BloomFilterUtils bloomFilterUtils;

    /**
     * 初始化布隆过滤器
     */
    @PostConstruct
    public void initBloomFilters() {
        log.info("开始初始化布隆过滤器...");
        
        // 初始化商品布隆过滤器
        bloomFilterUtils.createBloomFilter(
                BloomFilterUtils.ITEM_FILTER,
                CacheConstants.BLOOM_FILTER_EXPECTED_INSERTIONS,
                CacheConstants.BLOOM_FILTER_FPP
        );
        
        // 初始化用户布隆过滤器
        bloomFilterUtils.createBloomFilter(
                BloomFilterUtils.USER_FILTER,
                50000, // 预期用户数量
                0.01
        );
        
        // 初始化分类布隆过滤器
        bloomFilterUtils.createBloomFilter(
                BloomFilterUtils.CATEGORY_FILTER,
                1000, // 预期分类数量
                0.01
        );
        
        log.info("布隆过滤器初始化完成");
    }

    /**
     * 缓存监控配置（可选）
     */
    @Bean
    @ConditionalOnProperty(name = "cache.monitor.enabled", havingValue = "true", matchIfMissing = false)
    public CacheMonitor cacheMonitor() {
        return new CacheMonitor();
    }

    /**
     * 注册CacheUtils工具类为Spring Bean
     */
    @Bean
    public CacheUtils cacheUtils(StringRedisTemplate stringRedisTemplate) {
        return new CacheUtils(stringRedisTemplate);
    }

    /**
     * 缓存监控器
     */
    public static class CacheMonitor {
        
        @PostConstruct
        public void init() {
            log.info("缓存监控器已启用");
        }
        
        // 这里可以添加缓存监控相关的方法
        // 比如缓存命中率统计、缓存大小监控等
    }
}