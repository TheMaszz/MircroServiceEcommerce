package com.ecom.payment_service.client;

import com.ecom.common.bean.ApiResponse;
import com.ecom.common.bean.OrderBean;
import com.ecom.common.bean.PaymentStatusBean;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "order-service")
public interface OrderClient {
    @PutMapping("/apiendpoint/order/updateStage/{id}")
    ApiResponse updateStage(@PathVariable Long id, @RequestBody OrderBean orderBean);

    @PutMapping("/apiendpoint/order/updatePaymentStatus/{id}")
    ApiResponse updatePaymentStatus(@PathVariable Long id, @RequestBody PaymentStatusBean paymentStatusBean);

    @GetMapping("/apiendpoint/order/getBySession/{sessionId}")
    ApiResponse getBySession(@PathVariable String sessionId);
}