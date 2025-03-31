package com.ecom.common.bean;

import lombok.Data;

@Data
public class PaymentStatusBean {
    private Long id;
    private Long order_id;
    private String status;
    private String stripe_session_id;
    private String stripe_checkout_url;
}
