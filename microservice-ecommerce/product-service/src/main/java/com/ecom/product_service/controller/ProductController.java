package com.ecom.product_service.controller;

import com.ecom.product_service.bean.ApiResponse;
import com.ecom.product_service.bean.ProductBean;
import com.ecom.product_service.exception.BaseException;
import com.ecom.product_service.service.ProductService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/apiendpoint/product")
public class ProductController {

    private static final Logger log = LogManager.getLogger(ProductController.class);
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/getAll")
    public ApiResponse getProducts(
            @RequestParam(name = "search", required = false) String search,
            @RequestParam(name = "page_number", required = false) int page_number,
            @RequestParam(name = "page_size", required = false) int page_size,
            @RequestParam(name = "sort", required = false) String sort,
            @RequestParam(name = "sort_type", required = false) String sort_type
    ) throws BaseException {

        ApiResponse res = productService.getAllProduct(search, page_number, page_size, sort, sort_type);
        return res;
    }

    @GetMapping("/{id}")
    public ApiResponse getProductById(@PathVariable Long id) throws BaseException {
        ApiResponse res = productService.getProductById(id);
        return res;
    }

    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse createProduct(
            HttpServletRequest request,
            @RequestPart("product") String productJson,
            @RequestPart(value = "files", required = false) List<MultipartFile> files
    ) throws BaseException, JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        ProductBean productBean = objectMapper.readValue(productJson, ProductBean.class);

        ApiResponse res = productService.createProduct(productBean, files, request);
        return res;
    }

    @PutMapping(value = "/update/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse updateProduct(
            HttpServletRequest request,
            @PathVariable Long id,
            @RequestPart("product") String productJson,
            @RequestPart(value = "files", required = false) List<MultipartFile> files
    ) throws BaseException, JsonProcessingException {
        String userId = request.getHeader("X-User-Id");

        ObjectMapper objectMapper = new ObjectMapper();
        ProductBean productBean = objectMapper.readValue(productJson, ProductBean.class);

        ApiResponse res = productService.updateProduct(id, productBean, files, request);
        return res;
    }

    @DeleteMapping("/delete/{id}")
    public ApiResponse deleteProduct(
            HttpServletRequest request,
            @PathVariable Long id
    ) throws BaseException {
        ApiResponse res = productService.deleteProduct(id, request);
        return res;
    }

}
