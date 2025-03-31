package com.ecom.common.bean;

import com.ecom.common.dto.ProductRequest;
import lombok.Data;

import java.util.List;

@Data
public class CheckOutDTO {
    List<ProductRequest> productRequests;
    List<OrderPaymentDTO> ids;
}
