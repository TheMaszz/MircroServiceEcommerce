package com.ecom.common.dto;

import lombok.Data;

import java.util.List;

@Data
public class OrderRequest {
    private Long created_by;
    private Long address_id;
    private Double total_amount;
    private String stage;
    private List<Product> products;

    @Data
    public static class Product{
        private Long product_id;
        private Long qty;
    }
}
