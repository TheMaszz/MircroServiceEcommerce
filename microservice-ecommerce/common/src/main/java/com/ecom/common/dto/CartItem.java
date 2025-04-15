package com.ecom.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItem {
    private Long created_by;
    private String created_user;
    private List<CartProduct> products;
    private Boolean selected;
}
