package com.hmall.api.client.fallback;

import com.hmall.api.client.ItemClient;

import com.hmall.api.domain.dto.ItemDTO;
import com.hmall.api.domain.dto.OrderDetailDTO;
import com.hmall.api.domain.query.ItemPageQuery;
import com.hmall.common.domain.PageDTO;
import com.hmall.common.exception.BizIllegalException;
import com.hmall.common.utils.CollUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;

import java.util.Collection;
import java.util.List;

@Slf4j
public class ItemClientFallback implements FallbackFactory<ItemClient> {
    @Override
    public ItemClient create(Throwable cause) {
        return new ItemClient() {
            @Override
            public List<ItemDTO> queryItemByIds(Collection<Long> ids) {
                log.error("远程调用ItemClient#queryItemByIds方法出现异常，参数：{}", ids, cause);
                // 查询购物车允许失败，查询失败，返回空集合
                return CollUtils.emptyList();
            }

            @Override
            public void deductStock(List<OrderDetailDTO> items) {
                // 库存扣减业务需要触发事务回滚，查询失败，抛出异常
                throw new BizIllegalException(cause);
            }

            @Override
            public PageDTO<ItemDTO> search(ItemPageQuery query) {
                throw new BizIllegalException(cause);
            }

            @Override
            public void addStock(List<OrderDetailDTO> detailDTOS) {
                // 库存增加业务需要触发事务回滚，查询失败，抛出异常
                log.error("远程调用ItemClient#addStock方法出现异常，参数：{}", detailDTOS, cause);
                throw new BizIllegalException(cause);
            }

            @Override
            public ItemDTO queryItemById(Long id) {
                log.error("远程调用ItemClient#queryItemById方法出现异常，参数：{}", id, cause);
                return null;
            }


        };
    }
}