package com.ecom.common.bean;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderBean {
    private Long id;
    private Long user_id;
    private Long address_id;
    private Long shop_id;
    private String stage;
    private Double total_amount;
    private String created_at;
    private String updated_at;
    private List<OrderProduct> products;
    private PaymentStatusBean paymentStatus;

    private Long payment_status_id;
    private String shop_name;

    @Data
    static public class OrderProduct {
        private Long id;
        private Long order_id;
        private Long product_id;
        private Long qty;

        // Detail
        private ProductBean productDetail;
    }

}
