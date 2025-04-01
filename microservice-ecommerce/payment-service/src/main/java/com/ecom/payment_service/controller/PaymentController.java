package com.ecom.payment_service.controller;

import com.ecom.common.dto.CheckOutDTO;
import com.ecom.common.dto.StripeResponse;
import com.ecom.payment_service.service.PaymentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/apiendpoint/payment")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/checkout")
    public ResponseEntity<StripeResponse> checkout(
            @RequestBody CheckOutDTO checkOutDTO
    ) {
        StripeResponse stripeResponse = paymentService.checkoutProducts(checkOutDTO.getProductRequests(), checkOutDTO.getIds());
        return ResponseEntity.status(HttpStatus.OK).body(stripeResponse);
    }

    @PostMapping("/webhook")
    public ResponseEntity<String> handleStripeEvent(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String sigHeader
    ) {
        String res = paymentService.handleStripeEvent(payload, sigHeader);
        return ResponseEntity.status(HttpStatus.OK).body(res);
    }

    @PostMapping("/expire/{sessionId}")
    public ResponseEntity<String> expireSession(@PathVariable String sessionId) {
        String res = paymentService.expireBySessionId(sessionId);
        return ResponseEntity.ok(res);
    }


}
