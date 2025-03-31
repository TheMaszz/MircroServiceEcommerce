package com.ecom.payment_service.service;

import com.ecom.common.bean.ApiResponse;
import com.ecom.common.bean.OrderBean;
import com.ecom.common.bean.OrderPaymentDTO;
import com.ecom.common.bean.PaymentStatusBean;
import com.ecom.common.dto.ProductRequest;
import com.ecom.common.dto.StripeResponse;
import com.ecom.payment_service.client.OrderClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import com.stripe.param.checkout.SessionCreateParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PaymentService {

    private final OrderClient orderClient;

    @Value("${stripe.secretKey}")
    private String STRIPE_SECRET_KEY;

    @Value("${stripe.endpointSecretKey}")
    private String STRIPE_ENDPOINT_SECRET;

    public PaymentService(OrderClient orderClient) {
        this.orderClient = orderClient;
    }

    public StripeResponse checkoutProducts(List<ProductRequest> productRequests, List<OrderPaymentDTO> ids) {
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
                .setSuccessUrl("http://localhost:4200/checkout/success")
                .setCancelUrl("http://localhost:4200/checkout/cancel")
                .addAllLineItem(lineItems)
                .build();

        Session session = null;

        try {
            session = Session.create(params);
        } catch (StripeException e) {
            System.out.println(e.getMessage());
        }

        Session finalSession = session;
        ids.forEach(id -> {
            PaymentStatusBean paymentStatusBean = new PaymentStatusBean();
            OrderBean orderBean = new OrderBean();
            orderBean.setStage("Payment");
            paymentStatusBean.setStatus("Unpaid");
            paymentStatusBean.setStripe_session_id(finalSession.getId());
            paymentStatusBean.setStripe_checkout_url(finalSession.getUrl());

            orderClient.updateStage(id.getOrderId(), orderBean);
            orderClient.updatePaymentStatus(id.getPaymentStatusId(), paymentStatusBean);
        });


        return StripeResponse.builder()
                .status("SUCCESS")
                .message("Payment Session Created")
                .sessionId(session.getId())
                .sessionUrl(session.getUrl())
                .build();
    }

    public String handleStripeEvent(String payload, String sigHeader) {
        Event event;
        try {
            event = Webhook.constructEvent(
                    payload,
                    sigHeader,
                    STRIPE_ENDPOINT_SECRET
            );
            switch (event.getType()) {
                case "checkout.session.completed":
                    // mark order = paid
                    handleCompleted(event);
                    break;
                case "checkout.session.expired":
                    // mark order = failed
                    handleExpired(event);
                    break;
                default:
                    // log อื่นๆ เช่น ignored events
                    break;
            }
            return "Stripe endpoint connected";
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void handleCompleted(Event event) {
        Session session = (Session) event.getDataObjectDeserializer()
                .getObject()
                .orElse(null);

        String sessionId = session.getId();

        ApiResponse orderRes = orderClient.getBySession(sessionId);
        ObjectMapper objectMapper = new ObjectMapper();
        List<OrderBean> orders = objectMapper.convertValue(
                orderRes.getData(),
                objectMapper.getTypeFactory().constructCollectionType(List.class, OrderBean.class)
        );

        orders.forEach(order -> {
            OrderBean orderBean = new OrderBean();
            PaymentStatusBean paymentStatusBean = new PaymentStatusBean();
            orderBean.setStage("Preparing");
            paymentStatusBean.setStatus("Paid");
            paymentStatusBean.setStripe_checkout_url(null);
            paymentStatusBean.setStripe_session_id(null);

            orderClient.updateStage(order.getId(), orderBean);
            orderClient.updatePaymentStatus(order.getPayment_status_id(), paymentStatusBean);
        });

    }

    private void handleExpired(Event event) {
        Session session = (Session) event.getDataObjectDeserializer()
                .getObject()
                .orElse(null);

        String sessionId = session.getId();

        ApiResponse orderRes = orderClient.getBySession(sessionId);
        ObjectMapper objectMapper = new ObjectMapper();
        List<OrderBean> orders = objectMapper.convertValue(
                orderRes.getData(),
                objectMapper.getTypeFactory().constructCollectionType(List.class, OrderBean.class)
        );

        orders.forEach(order -> {
            OrderBean orderBean = new OrderBean();
            PaymentStatusBean paymentStatusBean = new PaymentStatusBean();
            orderBean.setStage("Cancelled");
            paymentStatusBean.setStatus("Failed");
            paymentStatusBean.setStripe_checkout_url(null);
            paymentStatusBean.setStripe_session_id(null);

            orderClient.updateStage(order.getId(), orderBean);
            orderClient.updatePaymentStatus(order.getPayment_status_id(), paymentStatusBean);
        });
    }

    public String expireBySessionId(String sessionId) {
        Stripe.apiKey = STRIPE_SECRET_KEY;
        try {
            // Retrieve session
            Session session = Session.retrieve(sessionId);

            // Properly expire the session using the expire() method
            Session expiredSession = session.expire();

            return "Session expired: " + expiredSession.getId();
        } catch (StripeException e) {
            return "Error: " + e.getMessage();
        }
    }


}
