package com.ecom.common.dto;

import lombok.Data;

import java.util.HashMap;

@Data
public class DashboardDataDTO {
    private HashMap<String, Object> count_user_by_role;
    private Long count_products;
    private Long count_orders;
}
