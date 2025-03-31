package com.ecom.common.bean;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OrderPaymentDTO {
    private Long orderId;
    private Long paymentStatusId;
}
