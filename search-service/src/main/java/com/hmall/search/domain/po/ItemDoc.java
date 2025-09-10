package com.hmall.search.domain.po;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

/**
 * 商品文档实体类
 */
@Data
@Document(indexName = "hmall")
public class ItemDoc {
    
    @Id
    private Long id;
    
    @Field(type = FieldType.Text, analyzer = "ik_max_word")
    private String name;
    
    @Field(type = FieldType.Keyword, index = false)
    private String image;
    
    @Field(type = FieldType.Keyword)
    private String category;
    
    @Field(type = FieldType.Keyword)
    private String brand;
    
    @Field(type = FieldType.Integer)
    private Integer sold;
    
    @Field(type = FieldType.Integer)
    private Integer commentCount;
    
    @Field(type = FieldType.Boolean)
    private Boolean isAD;
    
    @Field(type = FieldType.Integer)
    private Integer status;
    
    @Field(type = FieldType.Integer)
    private Integer price;
    
    @Field(type = FieldType.Date)
    private String updateTime;
}