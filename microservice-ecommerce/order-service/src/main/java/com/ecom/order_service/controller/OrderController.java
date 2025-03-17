package com.ecom.order_service.controller;

import com.ecom.order_service.bean.ApiResponse;
import com.ecom.order_service.bean.OrderBean;
import com.ecom.order_service.exception.BaseException;
import com.ecom.order_service.repository.OrderRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/apiendpoint/order")
public class OrderController {

    private final OrderRepository orderRepository;

    public OrderController(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @GetMapping("/getAll")
    public ApiResponse getALl(
            HttpServletRequest request
    ) throws BaseException {
        ApiResponse res = new ApiResponse();
        return res;
    }

    @GetMapping("/{id}")
    public ApiResponse getById(
            HttpServletRequest request,
            @PathVariable Long id
    ) throws BaseException {
        ApiResponse res = new ApiResponse();
        return res;
    }

    @PostMapping("/create")
    public ApiResponse create(
            HttpServletRequest request,
            @RequestBody OrderBean orderBean
    ) throws BaseException {
        ApiResponse res = new ApiResponse();
        return res;
    }

    @PutMapping("/update/{id}")
    public ApiResponse update(
            HttpServletRequest request,
            @PathVariable Long id,
            @RequestBody OrderBean orderBean
    ) throws BaseException {
        ApiResponse res = new ApiResponse();
        return res;
    }

    @PutMapping("/pushStage/{id}")
    public ApiResponse pushStage(
            HttpServletRequest request,
            @PathVariable Long id,
            @RequestBody OrderBean orderBean
    ) throws BaseException {
        ApiResponse res = new ApiResponse();
        return res;
    }


}
