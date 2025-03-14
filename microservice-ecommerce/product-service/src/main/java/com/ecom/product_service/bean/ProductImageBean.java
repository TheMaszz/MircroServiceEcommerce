package com.ecom.product_service.bean;

import lombok.Data;

@Data
public class ProductImageBean {
        private Long id;
        private Long product_id;
        private String image_url;
}
