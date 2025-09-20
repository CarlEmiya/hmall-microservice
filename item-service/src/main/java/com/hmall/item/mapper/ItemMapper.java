package com.hmall.item.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import com.hmall.item.domain.dto.OrderDetailDTO;
import com.hmall.item.domain.po.Item;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * <p>
 * 商品表 Mapper 接口
 * </p>
 *
 * @author 虎哥
 * @since 2023-05-05
 */
public interface ItemMapper extends BaseMapper<Item> {

    @Update("UPDATE item SET stock = stock - #{num} WHERE id = #{itemId}")
    void updateStock(OrderDetailDTO orderDetail);

//    加库存
    @Update("UPDATE item SET stock = stock + #{num} WHERE id = #{itemId}")
    void addStock(OrderDetailDTO orderDetail);
    
    /**
     * 查询所有商品ID，用于布隆过滤器初始化
     */
    @Select("SELECT id FROM item WHERE status = 1")
    List<Long> selectAllIds();
}
