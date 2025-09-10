package com.hmall.api.domain.po;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TradeMessage {

    private Order order;

    private List<OrderDetail> orderDetail;

    private Long userId;

//    private String token;

}
