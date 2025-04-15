package com.ecom.common.bean;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class ProductBean implements Serializable {
    private Long id;
    private String name;
    private String description;
    private Long qty;
    private double price;
    private String created_at;
    private String updated_at;
    private Long created_by;
    private Long updated_by;
    private String created_user;
    private String updated_user;
    private List<ProductImageBean> product_image;
}
