package com.ecom.payment_service.controller;

import com.ecom.common.dto.ProductRequest;
import com.ecom.common.dto.StripeResponse;
import com.ecom.payment_service.service.PaymentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/apiendpoint/payment")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/checkout")
    public ResponseEntity<StripeResponse> checkout(
            @RequestBody List<ProductRequest> productRequest
    ) {
        StripeResponse stripeResponse = paymentService.checkoutProducts(productRequest);
        return ResponseEntity.status(HttpStatus.OK).body(stripeResponse);
    }
}
