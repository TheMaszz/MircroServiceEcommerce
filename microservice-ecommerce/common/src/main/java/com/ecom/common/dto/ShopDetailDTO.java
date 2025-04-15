package com.ecom.common.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class ShopDetailDTO implements Serializable {
    private Long count_products;
    private Long id;
    private String username;
    private String profile_url;
    private String last_login;
}
