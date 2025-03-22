package com.ecom.payment_service.controller;

import com.ecom.common.dto.ProductRequest;
import com.ecom.common.dto.StripeResponse;
import com.ecom.payment_service.service.PaymentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/apiendpoint/payment")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/checkout/{orderId}")
    public ResponseEntity<StripeResponse> checkout(
            @RequestBody List<ProductRequest> productRequest,
            @PathVariable Long orderId
    ) {
        StripeResponse stripeResponse = paymentService.checkoutProducts(productRequest, orderId);
        return ResponseEntity.status(HttpStatus.OK).body(stripeResponse);
    }

    @GetMapping("/webhook")
    public ResponseEntity<String> handleStripeEvent(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String sigHeader
    ) {
        String res = paymentService.handleStripeEvent(payload, sigHeader);
        return ResponseEntity.status(HttpStatus.OK).body(res);
    }


}
