package com.ecom.payment_service.service;

import com.ecom.common.bean.ApiResponse;
import com.ecom.common.bean.OrderBean;
import com.ecom.common.dto.ProductRequest;
import com.ecom.common.dto.StripeResponse;
import com.ecom.payment_service.client.OrderClient;
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

    @Value("${stripe.secretKey}")
    private String STRIPE_SECRET_KEY;

    // YOUR_STRIPE_ENDPOINT_SECRET
    private final String STRIPE_ENDPOINT_SECRET = "YOUR_STRIPE_ENDPOINT_SECRET";

    private final OrderClient orderClient;

    public PaymentService(OrderClient orderClient) {
        this.orderClient = orderClient;
    }

    public StripeResponse checkoutProducts(List<ProductRequest> productRequests, Long orderId) {
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

        OrderBean orderBean = new OrderBean();
        orderBean.setStage("Payment");
        orderBean.setStripe_session_id(session.getId());
        orderBean.setStripe_checkout_url(session.getUrl());
        orderClient.updateOrder(orderId, orderBean);

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
        OrderBean order = (OrderBean) orderRes.getData();

        OrderBean orderBean = new OrderBean();
        orderBean.setStage("Preparing");
        orderBean.setPayment_status("Paid");
        orderBean.setStripe_checkout_url(null);
        orderBean.setStripe_session_id(null);

        orderClient.updateOrder(order.getId(), orderBean);
    }

    private void handleExpired(Event event) {
        Session session = (Session) event.getDataObjectDeserializer()
                .getObject()
                .orElse(null);

        String sessionId = session.getId();

        ApiResponse orderRes = orderClient.getBySession(sessionId);
        OrderBean order = (OrderBean) orderRes.getData();

        OrderBean orderBean = new OrderBean();
        orderBean.setStage("Cancelled");
        orderBean.setPayment_status("Failed");
        orderBean.setStripe_checkout_url(null);
        orderBean.setStripe_session_id(null);

        orderClient.updateOrder(order.getId(), orderBean);
    }

}
