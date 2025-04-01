package com.ecom.common.dto;

import lombok.Data;

import java.util.List;

@Data
public class CheckOutDTO {
    List<ProductRequest> productRequests;
    List<OrderPaymentDTO> ids;
}
