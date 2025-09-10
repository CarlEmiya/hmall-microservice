package com.hmall.search.domain.dto;

import com.hmall.common.domain.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 商品搜索查询条件
 */
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class ItemPageQuery extends PageQuery {
    
    /**
     * 搜索关键字
     */
    private String key;
    
    /**
     * 品牌
     */
    private String brand;
    
    /**
     * 分类
     */
    private String category;
    
    /**
     * 最低价格
     */
    private Integer minPrice;
    
    /**
     * 最高价格
     */
    private Integer maxPrice;
    
    /**
     * 排序字段：price、sold、updateTime
     */
    private String sortBy;
    
    /**
     * 是否升序
     */
    private Boolean isAsc = true;
}