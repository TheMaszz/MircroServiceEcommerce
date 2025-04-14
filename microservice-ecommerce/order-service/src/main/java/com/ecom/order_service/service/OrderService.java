package com.ecom.order_service.service;

import com.ecom.common.bean.ApiResponse;
import com.ecom.common.bean.OrderBean;
import com.ecom.common.bean.PaymentStatusBean;
import com.ecom.common.controller.BaseController;
import com.ecom.common.dto.OrderPaymentDTO;
import com.ecom.common.dto.OrderRequest;
import com.ecom.common.exception.BaseException;
import com.ecom.order_service.exception.OrderException;
import com.ecom.order_service.repository.OrderRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService extends BaseController {

    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public ApiResponse getMyShopOrders(
            HttpServletRequest request,
            String search,
            int page_number,
            int page_size,
            String sort,
            String sort_type,
            String stage,
            String start_date,
            String end_date
    ) throws BaseException {
        ApiResponse res = new ApiResponse();
        HashMap<String, Object> params = new HashMap<>();
        try {
            String userId = request.getHeader("X-User-Id");
            params.put("shop_id", userId);
            if (search != null && !search.isEmpty()) {
                params.put("search", search);
            }
            if (stage != null && !stage.isEmpty()) {
                if (stage.contains("||")) {
                    String[] stages = stage.split("\\|\\|");
                    params.put("stageList", Arrays.asList(stages));
                } else {
                    params.put("stage", stage);
                }
            }
            if(start_date != null && end_date != null){
                params.put("start_date", start_date);
                params.put("end_date", end_date);
            }

            this.pagination(page_number, page_size, sort, sort_type, params);
            params.put("isCount", false);
            List<OrderBean> orders = orderRepository.findMyOrders(params);
            params.put("isCount", true);
            List<OrderBean> ordersCount = orderRepository.findMyOrders(params);
            res.getPaginate().setLimit(page_size);
            res.getPaginate().setPage(page_number);
            res.getPaginate().setTotal(ordersCount.size());

            res.setData(orders);
        } catch (Exception e) {
            this.checkException(e, res);
        }
        return res;
    }

    public ApiResponse getMyOrders(
            HttpServletRequest request,
            String search,
            int page_number,
            int page_size,
            String sort,
            String sort_type,
            String stage
    ) throws BaseException {
        ApiResponse res = new ApiResponse();
        HashMap<String, Object> params = new HashMap<>();
        try {
            String userId = request.getHeader("X-User-Id");
            params.put("user_id", userId);
            if (search != null && !search.isEmpty()) {
                params.put("search", search);
            }
            if (stage != null && !stage.isEmpty()) {
                if (stage.contains("||")) {
                    String[] stages = stage.split("\\|\\|");
                    params.put("stageList", Arrays.asList(stages));
                } else {
                    params.put("stage", stage); // Single stage
                }
            }

            this.pagination(page_number, page_size, sort, sort_type, params);
            params.put("isCount", false);
            List<OrderBean> orders = orderRepository.findMyOrders(params);
            params.put("isCount", true);
            List<OrderBean> ordersCount = orderRepository.findMyOrders(params);
            res.getPaginate().setLimit(page_size);
            res.getPaginate().setPage(page_number);
            res.getPaginate().setTotal(ordersCount.size());

            res.setData(orders);
        } catch (Exception e) {
            this.checkException(e, res);
        }
        return res;
    }

    public ApiResponse getAll(
            String search,
            int page_number,
            int page_size,
            String sort,
            String sort_type,
            String stage,
            String start_date,
            String end_date
    ) throws BaseException {
        ApiResponse res = new ApiResponse();
        HashMap<String, Object> params = new HashMap<>();
        try {
            if (search != null && !search.isEmpty()) {
                params.put("search", search);
            }
            if (stage != null && !stage.isEmpty()) {
                if (stage.contains("||")) {
                    String[] stages = stage.split("\\|\\|");
                    params.put("stageList", Arrays.asList(stages));
                } else {
                    params.put("stage", stage); // Single stage
                }
            }
            if(start_date != null && end_date != null){
                params.put("start_date", start_date);
                params.put("end_date", end_date);
            }
            
            this.pagination(page_number, page_size, sort, sort_type, params);
            params.put("isCount", false);
            List<OrderBean> orders = orderRepository.findAll(params);
            params.put("isCount", true);
            List<OrderBean> ordersCount = orderRepository.findAll(params);
            res.getPaginate().setLimit(page_size);
            res.getPaginate().setPage(page_number);
            res.getPaginate().setTotal(ordersCount.size());

            res.setData(orders);
        } catch (Exception e) {
            this.checkException(e, res);
        }
        return res;
    }

    public ApiResponse getById(
            Long id
    ) throws BaseException {
        ApiResponse res = new ApiResponse();
        try {
            OrderBean order = orderRepository.findById(id);
            if (order == null) {
                throw new OrderException("not.found", "order not found");
            }
            res.setData(order);
        } catch (Exception e) {
            this.checkException(e, res);
        }
        return res;
    }

    public ApiResponse createOrder(
            HttpServletRequest request,
            List<OrderRequest> orderRequestList
    ) throws BaseException {
        ApiResponse res = new ApiResponse();
        try {
            String userId = request.getHeader("X-User-Id");
            String role = request.getHeader("X-Role");

            List<OrderBean> orderBeanList = new ArrayList<>();

            orderRequestList.forEach(orderRequest -> {
                OrderBean orderBean = new OrderBean();
                orderBean.setUser_id(Long.valueOf(userId));
                orderBean.setAddress_id(orderRequest.getAddress_id());
                orderBean.setShop_id(orderRequest.getCreated_by());
                orderBean.setTotal_amount(orderRequest.getTotal_amount());
                orderBean.setStage(orderRequest.getStage());
                orderBeanList.add(orderBean);
            });


            orderRepository.createOrder(orderBeanList);

            for (int index = 0; index < orderRequestList.size(); index++) {
                OrderRequest orderRequest = orderRequestList.get(index);
                OrderBean orderBean = orderBeanList.get(index);

                if (orderRequest.getProducts() != null && !orderRequest.getProducts().isEmpty()) {
                    List<OrderBean.OrderProduct> orderProducts = new ArrayList<>();

                    orderRequest.getProducts().forEach(product -> {
                        OrderBean.OrderProduct orderProduct = new OrderBean.OrderProduct();
                        orderProduct.setOrder_id(orderBean.getId());
                        orderProduct.setProduct_id(product.getProduct_id());
                        orderProduct.setQty(product.getQty());
                        orderProducts.add(orderProduct);
                    });

                    orderRepository.createOrderProduct(orderProducts);
                }


                PaymentStatusBean paymentStatusBean = new PaymentStatusBean();
                paymentStatusBean.setOrder_id(orderBean.getId());
                orderRepository.createPaymentStatus(paymentStatusBean);

                orderBean.setPayment_status_id(paymentStatusBean.getId());
            }

            List<OrderPaymentDTO> ids = orderBeanList.stream()
                    .map(order -> new OrderPaymentDTO(order.getId(), order.getPayment_status_id()))
                    .collect(Collectors.toList());

            res.setData(ids);
        } catch (Exception e) {
            this.checkException(e, res);
        }
        return res;
    }

    public ApiResponse updateOrder(
            Long id,
            OrderBean orderBean
    ) throws BaseException {
        ApiResponse res = new ApiResponse();
        try {
            OrderBean order = orderRepository.findById(id);
            if (order == null) {
                throw new OrderException("not.found", "order not found");
            }

            orderBean.setId(id);
            orderRepository.updateOrder(orderBean);
        } catch (Exception e) {
            this.checkException(e, res);
        }
        return res;
    }

    public ApiResponse updateStage(
            Long id,
            OrderBean orderBean
    ) throws BaseException {
        ApiResponse res = new ApiResponse();
        try {
            OrderBean order = orderRepository.findById(id);
            if (order == null) {
                throw new OrderException("not.found", "order not found");
            }

            List<String> validStages = Arrays.asList(
                    "Pending",
                    "Payment",
                    "Preparing",
                    "Shipping",
                    "Delivered",
                    "Completed",
                    "Cancelled"
            );

            if (!validStages.contains(orderBean.getStage())) {
                throw new OrderException("invalid.stage", "Invalid stage: " + orderBean.getStage());
            }

            orderRepository.updateStage(id, orderBean.getStage());
        } catch (Exception e) {
            this.checkException(e, res);
        }
        return res;
    }

    public ApiResponse updatePaymentStatus(
            Long id,
            PaymentStatusBean paymentStatusBean
    ) throws BaseException {
        ApiResponse res = new ApiResponse();
        try {
            PaymentStatusBean paymentStatus = orderRepository.findPaymentStatusById(id);
            if (paymentStatus == null) {
                throw new OrderException("paymentStatus.not.found", "order paymentStatus not found");
            }

            List<String> validPaymentStatus = Arrays.asList(
                    "Unpaid",
                    "Paid",
                    "Failed",
                    "Refunded"
            );

            if (!validPaymentStatus.contains(paymentStatusBean.getStatus())) {
                throw new OrderException("invalid.payment_status", "Invalid payment_status: " + paymentStatusBean.getStatus());
            }

            paymentStatusBean.setId(id);
            orderRepository.updatePaymentStatus(paymentStatusBean);
        } catch (Exception e) {
            this.checkException(e, res);
        }
        return res;
    }

    public ApiResponse getBySession(String sessionId) throws BaseException {
        ApiResponse res = new ApiResponse();
        try {
            List<OrderPaymentDTO> orders = orderRepository.findBySessionId(sessionId);
            if (orders == null || orders.isEmpty()) {
                throw new OrderException("not.found", "order not found");
            }
            res.setData(orders);
        } catch (Exception e) {
            this.checkException(e, res);
        }
        return res;
    }


}
