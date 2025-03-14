package com.ecom.product_service.bean;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class ProductBean {
    private Long id;
    private String name;
    private String description;
    private Long qty;
    private double price;
    private String created_at;
    private String updated_at;
    private Long created_by;
    private Long updated_by;
    private List<ProductImageBean> product_image;
}
