package com.ecom.payment_service.service;

import com.ecom.common.dto.ProductRequest;
import com.ecom.common.dto.StripeResponse;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PaymentService {

    @Value("${stripe.secretKey}")
    private String STRIPE_SECRET_KEY;

    public StripeResponse checkoutProducts(List<ProductRequest> productRequests) {
        Stripe.apiKey = STRIPE_SECRET_KEY;

        List<SessionCreateParams.LineItem> lineItems = new ArrayList<>();

        for (ProductRequest productRequest : productRequests) {
            SessionCreateParams.LineItem.PriceData.ProductData productData = SessionCreateParams.LineItem.PriceData.ProductData.builder()
                    .setName(productRequest.getName())
                    .build();

            SessionCreateParams.LineItem.PriceData priceData = SessionCreateParams.LineItem.PriceData.builder()
                    .setCurrency(productRequest.getCurrency() == null ? "THB" : productRequest.getCurrency())
                    .setUnitAmount(productRequest.getAmount() * 100)
                    .setProductData(productData)
                    .build();

            SessionCreateParams.LineItem lineItem = SessionCreateParams.LineItem.builder()
                    .setQuantity(productRequest.getQuantity())
                    .setPriceData(priceData)
                    .build();

            lineItems.add(lineItem);
        }

        SessionCreateParams params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl("http://localhost:8084/success")
                .setCancelUrl("http://localhost:8084/cancel")
                .addAllLineItem(lineItems)
                .build();

        Session session = null;

        try {
            session = Session.create(params);
        } catch (StripeException e) {
            System.out.println(e.getMessage());
        }

        return StripeResponse.builder()
                .status("SUCCESS")
                .message("Payment Session Created")
                .sessionId(session.getId())
                .sessionUrl(session.getUrl())
                .build();
    }

}
