package com.ecom.common.dto;

import lombok.Data;

@Data
public class ShopDetailDTO {
    private Long count_products;
    private Long id;
    private String username;
    private String profile_url;
    private String last_login;
}
