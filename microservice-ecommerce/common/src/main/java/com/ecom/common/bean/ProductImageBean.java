package com.ecom.common.bean;

import lombok.Data;

import java.io.Serializable;

@Data
public class ProductImageBean implements Serializable {
        private Long id;
        private Long product_id;
        private String image_url;
}
