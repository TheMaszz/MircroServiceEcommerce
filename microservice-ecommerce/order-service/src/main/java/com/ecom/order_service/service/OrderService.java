package com.ecom.order_service.service;

import com.ecom.common.bean.ApiResponse;
import com.ecom.common.bean.OrderBean;
import com.ecom.common.controller.BaseController;
import com.ecom.common.exception.BaseException;
import com.ecom.order_service.exception.OrderException;
import com.ecom.order_service.repository.OrderRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

@Service
public class OrderService extends BaseController {

    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public ApiResponse getMyOrders(
            HttpServletRequest request,
            String search,
            int page_number,
            int page_size,
            String sort,
            String sort_type
    ) throws BaseException {
        ApiResponse res = new ApiResponse();
        HashMap<String, Object> params = new HashMap<>();
        try {
            String userId = request.getHeader("X-User-Id");
            params.put("user_id", userId);
            if (search != null && !search.isEmpty()) {
                params.put("search", search);
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
            String sort_type
    ) throws BaseException {
        ApiResponse res = new ApiResponse();
        HashMap<String, Object> params = new HashMap<>();
        try {
            if (search != null && !search.isEmpty()) {
                params.put("search", search);
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
            OrderBean orderBean
    ) throws BaseException {
        ApiResponse res = new ApiResponse();
        try {
            orderRepository.createOrder(orderBean);

            if (orderBean.getProducts() != null && !orderBean.getProducts().isEmpty()) {
                orderBean.getProducts().forEach(product -> product.setOrder_id(orderBean.getId()));
                orderRepository.createOrderProduct(orderBean.getProducts());
            }

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
            OrderBean orderBean
    ) throws BaseException {
        ApiResponse res = new ApiResponse();
        try {
            OrderBean order = orderRepository.findById(id);
            if (order == null) {
                throw new OrderException("not.found", "order not found");
            }

            List<String> validPaymentStatus = Arrays.asList(
                    "Unpaid",
                    "Paid",
                    "Failed",
                    "Refunded"
            );

            if (!validPaymentStatus.contains(orderBean.getPayment_status())) {
                throw new OrderException("invalid.payment_status", "Invalid payment_status: " + orderBean.getPayment_status());
            }

            orderRepository.updatePaymentStatus(id, orderBean.getPayment_status());
        } catch (Exception e) {
            this.checkException(e, res);
        }
        return res;
    }


}
