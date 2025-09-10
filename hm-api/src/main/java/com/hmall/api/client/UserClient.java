package com.hmall.api.client;


import com.hmall.api.client.fallback.UserClientFallback;
import com.hmall.api.config.DefaultFeignConfig;
import com.hmall.api.domain.dto.LoginFormDTO;
import com.hmall.api.domain.po.User;
import com.hmall.api.domain.vo.UserLoginVO;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@FeignClient(value = "user-service", configuration = DefaultFeignConfig.class, fallbackFactory = UserClientFallback.class)
public interface UserClient {

    @ApiOperation("用户登录接口")
    @PostMapping("/users/login")
    UserLoginVO login(@RequestBody @Validated LoginFormDTO loginFormDTO);

    @ApiOperation("扣减余额")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pw", value = "支付密码"),
            @ApiImplicitParam(name = "amount", value = "支付金额")
    })
    @PutMapping("/users/money/deduct")
    void deductMoney(@RequestParam("pw") String pw, @RequestParam("amount") Integer amount);

    @GetMapping("{id}")
    User getUserById(@PathVariable("id") Long id);
}
