package com.ecom.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartProduct {
    private Long id;
    private String name;
    private Double price;
    private Integer qtyInStock;
    private Integer qty;
    private String imageUrl;
    private Long created_by;
    private String created_user;
    private Boolean selected;
    private Double totalPrice;
}