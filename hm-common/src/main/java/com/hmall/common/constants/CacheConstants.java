package com.hmall.common.constants;

public class CacheConstants {
    // 订单缓存键前缀，用于构建具体订单的缓存键
    public static final String ORDER_CACHE_KEY_PREFIX = "order:";

    // 订单缓存过期时间，单位：秒（这里设置为2小时，可根据实际需求调整）
    public static final Long ORDER_CACHE_TTL = 7200L;
    
    // 购物车缓存键前缀
    public static final String CART_KEY = "cart";
    // 购物车缓存过期时间,30分钟
    public static final Long CART_TTL = 30L;
    
    // 搜索结果缓存前缀：标识所有搜索结果相关缓存
    public static final String SEARCH_RESULT_KEY_PREFIX = "search:result:";

    // 搜索筛选条件缓存前缀：标识所有搜索筛选（如分类、价格区间、品牌等）相关缓存
    public static final String SEARCH_FILTERS_KEY_PREFIX = "search:filters:";
    
    // 搜索结果缓存过期时间，单位：秒（这里设置为1天，可根据实际需求调整）
    public static final Long SEARCH_RESULT_TTL = 86400L;
    
    // 搜索筛选条件缓存过期时间，单位：秒（这里设置为1天，可根据实际需求调整）
    public static final Long SEARCH_FILTERS_TTL = 86400L;
    public static final String USER_KEY = "user:";
    public static final Long USER_TTL = 7200L;   //2个小时
}
