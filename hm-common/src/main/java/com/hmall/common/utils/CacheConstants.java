package com.hmall.common.utils;

import java.util.concurrent.TimeUnit;

/**
 * 缓存常量类
 * 统一管理缓存键前缀、过期时间等常量
 */
public class CacheConstants {

    // ==================== 缓存键前缀 ====================
    
    // 商品相关缓存键
    public static final String ITEM_CACHE_KEY_PREFIX = "cache:item:";
    public static final String ITEM_STOCK_KEY_PREFIX = "cache:item:stock:";
    public static final String ITEM_LIST_KEY_PREFIX = "cache:item:list:";
    public static final String ITEM_HOT_KEY_PREFIX = "cache:item:hot:";
    public static final String ITEM_CATEGORY_KEY_PREFIX = "cache:item:category:";
    
    // 搜索相关缓存键
    public static final String SEARCH_RESULT_KEY_PREFIX = "cache:search:result:";
    public static final String SEARCH_HOT_WORDS_KEY = "cache:search:hot:words";
    public static final String SEARCH_SUGGEST_KEY_PREFIX = "cache:search:suggest:";
    
    // 购物车相关缓存键
    public static final String CART_KEY_PREFIX = "cache:cart:user:";
    public static final String CART_COUNT_KEY_PREFIX = "cache:cart:count:";
    
    // 用户相关缓存键
    public static final String USER_KEY_PREFIX = "cache:user:";
    public static final String USER_SESSION_KEY_PREFIX = "cache:user:session:";
    
    // 分类相关缓存键
    public static final String CATEGORY_KEY_PREFIX = "cache:category:";
    public static final String CATEGORY_TREE_KEY = "cache:category:tree";
    
    // 统计相关缓存键
    public static final String STATS_KEY_PREFIX = "cache:stats:";
    public static final String STATS_DAILY_KEY_PREFIX = "cache:stats:daily:";
    
    // ==================== 缓存过期时间 ====================
    
    // 商品相关过期时间
    public static final Long ITEM_CACHE_TTL = 30L;
    public static final TimeUnit ITEM_CACHE_TTL_UNIT = TimeUnit.MINUTES;
    
    public static final Long ITEM_HOT_TTL = 1L;
    public static final TimeUnit ITEM_HOT_TTL_UNIT = TimeUnit.HOURS;
    
    public static final Long ITEM_LIST_TTL = 10L;
    public static final TimeUnit ITEM_LIST_TTL_UNIT = TimeUnit.MINUTES;
    
    // 搜索相关过期时间
    public static final Long SEARCH_RESULT_TTL = 15L;
    public static final TimeUnit SEARCH_RESULT_TTL_UNIT = TimeUnit.MINUTES;
    
    public static final Long SEARCH_HOT_WORDS_TTL = 1L;
    public static final TimeUnit SEARCH_HOT_WORDS_TTL_UNIT = TimeUnit.HOURS;
    
    // 购物车相关过期时间
    public static final Long CART_TTL = 7L;
    public static final TimeUnit CART_TTL_UNIT = TimeUnit.DAYS;
    
    // 用户相关过期时间
    public static final Long USER_TTL = 30L;
    public static final TimeUnit USER_TTL_UNIT = TimeUnit.MINUTES;
    
    public static final Long USER_SESSION_TTL = 2L;
    public static final TimeUnit USER_SESSION_TTL_UNIT = TimeUnit.HOURS;
    
    // 分类相关过期时间
    public static final Long CATEGORY_TTL = 1L;
    public static final TimeUnit CATEGORY_TTL_UNIT = TimeUnit.HOURS;
    
    // 统计相关过期时间
    public static final Long STATS_TTL = 5L;
    public static final TimeUnit STATS_TTL_UNIT = TimeUnit.MINUTES;
    
    // ==================== 空值缓存时间 ====================
    public static final Long NULL_CACHE_TTL = 2L;
    public static final TimeUnit NULL_CACHE_TTL_UNIT = TimeUnit.MINUTES;
    
    // ==================== 锁相关常量 ====================
    public static final String LOCK_KEY_PREFIX = "lock:";
    public static final Long LOCK_TTL = 10L;
    public static final TimeUnit LOCK_TTL_UNIT = TimeUnit.SECONDS;
    
    // ==================== 延迟双删相关常量 ====================
    public static final Long DEFAULT_DELAY_DELETE_TIME = 500L; // 默认延迟删除时间（毫秒）
    public static final Long STOCK_DELAY_DELETE_TIME = 1000L; // 库存延迟删除时间（毫秒）
    
    // ==================== 布隆过滤器相关常量 ====================
    public static final int BLOOM_FILTER_EXPECTED_INSERTIONS = 100000; // 预期插入数量
    public static final double BLOOM_FILTER_FPP = 0.01; // 误判率 1%
    
    // ==================== 缓存预热相关常量 ====================
    public static final String CACHE_WARMUP_LOCK_KEY = "lock:cache:warmup";
    public static final Long CACHE_WARMUP_LOCK_TTL = 5L;
    public static final TimeUnit CACHE_WARMUP_LOCK_TTL_UNIT = TimeUnit.MINUTES;
    
    // ==================== 工具方法 ====================
    
    /**
     * 构建缓存键
     */
    public static String buildKey(String prefix, Object... params) {
        StringBuilder sb = new StringBuilder(prefix);
        for (Object param : params) {
            sb.append(param);
        }
        return sb.toString();
    }
    
    /**
     * 构建锁键
     */
    public static String buildLockKey(String key) {
        return LOCK_KEY_PREFIX + key;
    }
    
    /**
     * 构建商品缓存键
     */
    public static String buildItemKey(Long itemId) {
        return ITEM_CACHE_KEY_PREFIX + itemId;
    }
    
    /**
     * 构建商品库存缓存键
     */
    public static String buildItemStockKey(Long itemId) {
        return ITEM_STOCK_KEY_PREFIX + itemId;
    }
    
    /**
     * 构建购物车缓存键
     */
    public static String buildCartKey(Long userId) {
        return CART_KEY_PREFIX + userId;
    }
    
    /**
     * 构建搜索结果缓存键
     */
    public static String buildSearchResultKey(String keyword, Integer page, Integer size) {
        return SEARCH_RESULT_KEY_PREFIX + keyword + ":" + page + ":" + size;
    }
}