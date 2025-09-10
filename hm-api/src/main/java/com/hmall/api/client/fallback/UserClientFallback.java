package com.hmall.api.client.fallback;

import com.hmall.api.client.UserClient;
import com.hmall.api.domain.dto.LoginFormDTO;
import com.hmall.api.domain.po.User;
import com.hmall.api.domain.vo.UserLoginVO;

import com.hmall.common.exception.BizIllegalException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;

@Slf4j
public class UserClientFallback implements FallbackFactory<UserClient> {
    @Override
    public UserClient create(Throwable cause) {
        return new UserClient() {
            @Override
            public UserLoginVO login(LoginFormDTO loginFormDTO) {
                log.error("远程调用UserClient#login方法出现异常，参数：{}", loginFormDTO, cause);
                // 登录失败，抛出异常
                throw new BizIllegalException("用户服务暂时不可用，请稍后重试");
            }

            @Override
            public void deductMoney(String pw, Integer amount) {
                log.error("远程调用UserClient#deductMoney方法出现异常，参数：pw={}, amount={}", pw, amount, cause);
                // 扣款业务需要触发事务回滚，查询失败，抛出异常
                throw new BizIllegalException("用户服务暂时不可用，扣款失败: " + cause.getMessage());
            }

            @Override
            public User getUserById(Long id) {
                return null;
            }
        };
    }
}