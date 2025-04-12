package com.ecom.order_service.controller;

import com.ecom.common.bean.ApiResponse;
import com.ecom.common.bean.OrderBean;
import com.ecom.common.bean.PaymentStatusBean;
import com.ecom.common.bean.UserBean;
import com.ecom.common.dto.OrderRequest;
import com.ecom.common.exception.BaseException;
import com.ecom.order_service.service.OrderService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/apiendpoint/order")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/getMyShopOrders")
    public ResponseEntity<ApiResponse> getMyShopOrders(
            HttpServletRequest request,
            @RequestParam(name = "search", required = false) String search,
            @RequestParam(name = "page_number", required = false) int page_number,
            @RequestParam(name = "page_size", required = false) int page_size,
            @RequestParam(name = "sort", required = false) String sort,
            @RequestParam(name = "sort_type", required = false) String sort_type,
            @RequestParam(name = "stage", required = false) String stage,
            @RequestParam(name = "start_date", required = false) String start_date,
            @RequestParam(name = "end_date", required = false) String end_date
    ) throws BaseException {
        ApiResponse res = orderService.getMyShopOrders(request, search, page_number, page_size, sort, sort_type, stage, start_date, end_date);
        return ResponseEntity.status(res.getStatus()).body(res);
    }

    @GetMapping("/getMyOrders")
    public ResponseEntity<ApiResponse> getMyOrders(
            HttpServletRequest request,
            @RequestParam(name = "search", required = false) String search,
            @RequestParam(name = "page_number", required = false) int page_number,
            @RequestParam(name = "page_size", required = false) int page_size,
            @RequestParam(name = "sort", required = false) String sort,
            @RequestParam(name = "sort_type", required = false) String sort_type,
            @RequestParam(name = "stage", required = false) String stage
    ) throws BaseException {
        ApiResponse res = orderService.getMyOrders(request, search, page_number, page_size, sort, sort_type, stage);
        return ResponseEntity.status(res.getStatus()).body(res);
    }

    @GetMapping("/getAll")
    public ResponseEntity<ApiResponse> getALl(
            HttpServletRequest request,
            @RequestParam(name = "search", required = false) String search,
            @RequestParam(name = "page_number", required = false) int page_number,
            @RequestParam(name = "page_size", required = false) int page_size,
            @RequestParam(name = "sort", required = false) String sort,
            @RequestParam(name = "sort_type", required = false) String sort_type
    ) throws BaseException {
        ApiResponse res = orderService.getAll(search, page_number, page_size, sort, sort_type);
        return ResponseEntity.status(res.getStatus()).body(res);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getById(
            HttpServletRequest request,
            @PathVariable Long id
    ) throws BaseException {
        ApiResponse res = orderService.getById(id);
        return ResponseEntity.status(res.getStatus()).body(res);
    }

    @PostMapping("/create")
    public ResponseEntity<ApiResponse> create(
            HttpServletRequest request,
            @RequestBody List<OrderRequest> orderRequestList
    ) throws BaseException {
        ApiResponse res = orderService.createOrder(request, orderRequestList);
        return ResponseEntity.status(res.getStatus()).body(res);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<ApiResponse> update(
            HttpServletRequest request,
            @PathVariable Long id,
            @RequestBody OrderBean orderBean
    ) throws BaseException {
        ApiResponse res = orderService.updateOrder(id, orderBean);
        return ResponseEntity.status(res.getStatus()).body(res);
    }

    @PutMapping("/updateStage/{id}")
    public ResponseEntity<ApiResponse> updateStage(
            HttpServletRequest request,
            @PathVariable Long id,
            @RequestBody OrderBean orderBean
    ) throws BaseException {
        ApiResponse res = orderService.updateStage(id, orderBean);
        return ResponseEntity.status(res.getStatus()).body(res);
    }

    @PutMapping("/updatePaymentStatus/{id}")
    public ResponseEntity<ApiResponse> updatePaymentStatus(
            HttpServletRequest request,
            @PathVariable Long id,
            @RequestBody PaymentStatusBean paymentStatusBean
    ) throws BaseException {
        ApiResponse res = orderService.updatePaymentStatus(id, paymentStatusBean);
        return ResponseEntity.status(res.getStatus()).body(res);
    }

    @GetMapping("/getBySession/{sessionId}")
    public ResponseEntity<ApiResponse> getBySession(
            @PathVariable String sessionId
    ) throws BaseException {
        ApiResponse res = orderService.getBySession(sessionId);
        return ResponseEntity.status(res.getStatus()).body(res);
    }


}
