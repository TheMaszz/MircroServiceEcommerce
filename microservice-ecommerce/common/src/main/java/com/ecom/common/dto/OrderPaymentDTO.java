package com.ecom.common.dto;

import com.ecom.common.bean.OrderBean;
import lombok.Data;

import java.util.Collections;
import java.util.List;

@Data
public class OrderPaymentDTO {
    private Long orderId;
    private Long paymentStatusId;
    private List<OrderBean.OrderProduct> products;

    public OrderPaymentDTO() {
        this.products = Collections.emptyList();
    }

    public OrderPaymentDTO(Long orderId, Long paymentStatusId) {
        this.orderId = orderId;
        this.paymentStatusId = paymentStatusId;
        this.products = Collections.emptyList();
    }
}