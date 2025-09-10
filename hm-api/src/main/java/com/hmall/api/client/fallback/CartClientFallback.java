package com.hmall.api.client.fallback;

import com.hmall.api.client.CartClient;
import com.hmall.api.domain.dto.CartFormDTO;
import com.hmall.api.domain.po.Cart;
import com.hmall.api.domain.vo.CartVO;

import com.hmall.common.exception.BizIllegalException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;

import java.util.Collection;
import java.util.List;

@Slf4j
public class CartClientFallback implements FallbackFactory<CartClient> {
    @Override
    public CartClient create(Throwable cause) {
        return new CartClient() {
            @Override
            public void addItem2Cart(CartFormDTO cartFormDTO) {
                log.error("远程调用CartClient#addItem2Cart方法出现异常，参数：{}", cartFormDTO, cause);
                // 添加购物车商品业务需要触发事务回滚，抛出异常
                throw new BizIllegalException("购物车服务暂时不可用，添加购物车商品失败: " + cause.getMessage());
            }

            @Override
            public void updateCart(Cart cart) {
                log.error("远程调用CartClient#updateCart方法出现异常，参数：{}", cart, cause);
                // 修改购物车商品业务需要触发事务回滚，抛出异常
                throw new BizIllegalException("购物车服务暂时不可用，修改购物车商品失败: " + cause.getMessage());
            }

            @Override
            public void deleteCartItem(Long id) {
                log.error("远程调用CartClient#deleteCartItem方法出现异常，参数：{}", id, cause);
                // 删除购物车商品业务需要触发事务回滚，抛出异常
                throw new BizIllegalException("购物车服务暂时不可用，删除购物车商品失败: " + cause.getMessage());
            }

            @Override
            public List<CartVO> queryMyCarts() {
                return List.of();
            }

            @Override
            public void deleteCartItemByIds(Collection<Long> ids) {
                log.error("远程调用CartClient#deleteCartItemByIds方法出现异常，参数：{}", ids, cause);
                // 删除购物车商品业务需要触发事务回滚，抛出异常
                throw new BizIllegalException("购物车服务暂时不可用，删除购物车商品失败: " + cause.getMessage());
            }

            @Override
            public Boolean deleteCartItemByIdsAndUserId(Long userId, List<Long> itemIds) {
                log.error("远程调用CartClient#deleteCartItem方法出现异常，参数：{}", userId,itemIds, cause);
                // 删除购物车商品业务需要触发事务回滚，抛出异常
                throw new BizIllegalException("购物车服务暂时不可用，删除购物车商品失败: " + cause.getMessage());
            }

        };
    }
}