package com.hmall.common.utils;

import cn.hutool.core.util.StrUtil;

/**
 * 缓存键工具类
 * 用于构建各种业务场景的缓存键
 */
public class CacheKeyUtils {

    /**
     * 构建搜索结果缓存键
     * 
     * @param key 搜索关键词
     * @param category 分类
     * @param brand 品牌
     * @param minPrice 最低价格
     * @param maxPrice 最高价格
     * @param pageNo 页码
     * @param pageSize 页面大小
     * @param sortBy 排序字段
     * @param isAsc 是否升序
     * @return 缓存键
     */
    public static String buildSearchKey(String key, String category, String brand, 
                                      Integer minPrice, Integer maxPrice, 
                                      Integer pageNo, Integer pageSize, 
                                      String sortBy, Boolean isAsc) {
        StringBuilder sb = new StringBuilder("search:result:");
        
        // 搜索关键词
        sb.append("key:").append(StrUtil.isBlank(key) ? "all" : key).append(":");
        
        // 分类
        sb.append("cat:").append(StrUtil.isBlank(category) ? "all" : category).append(":");
        
        // 品牌
        sb.append("brand:").append(StrUtil.isBlank(brand) ? "all" : brand).append(":");
        
        // 价格区间
        sb.append("price:").append(minPrice == null ? "0" : minPrice)
          .append("-").append(maxPrice == null ? "max" : maxPrice).append(":");
        
        // 分页信息
        sb.append("page:").append(pageNo == null ? 1 : pageNo)
          .append("-").append(pageSize == null ? 20 : pageSize).append(":");
        
        // 排序信息
        sb.append("sort:").append(StrUtil.isBlank(sortBy) ? "default" : sortBy)
          .append("-").append(isAsc == null ? "desc" : (isAsc ? "asc" : "desc"));
        
        return sb.toString();
    }
    
    

    /**
     * 构建搜索过滤条件缓存键
     * 
     * @param key 搜索关键词
     * @param category 分类
     * @param brand 品牌
     * @return 缓存键
     */
    public static String buildSearchFiltersKey(String key, String category, String brand) {
        StringBuilder sb = new StringBuilder("search:filters:");
        
        // 搜索关键词
        sb.append("key:").append(StrUtil.isBlank(key) ? "all" : key).append(":");
        
        // 分类
        sb.append("cat:").append(StrUtil.isBlank(category) ? "all" : category).append(":");
        
        // 品牌
        sb.append("brand:").append(StrUtil.isBlank(brand) ? "all" : brand);
        
        return sb.toString();
    }


    public static String buildItemListKey(String key, String brand, String category, Integer minPrice, Integer maxPrice) {

        StringBuilder sb = new StringBuilder("item:list:");

        // 搜索关键词
        sb.append("key:").append(StrUtil.isBlank(key) ? "all" : key).append(":");

        // 品牌
        sb.append("brand:").append(StrUtil.isBlank(brand) ? "all" : brand).append(":");

        // 分类
        sb.append("cat:").append(StrUtil.isBlank(category) ? "all" : category).append(":");

        // 价格区间
        sb.append("price:").append(minPrice == null ? "0" : minPrice)
               .append("-").append(maxPrice == null ? "max" : maxPrice);

        return sb.toString();

    }
}