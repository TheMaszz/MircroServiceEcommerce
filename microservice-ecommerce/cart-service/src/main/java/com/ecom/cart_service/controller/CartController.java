package com.ecom.cart_service.controller;

import com.ecom.cart_service.service.CartService;
import com.ecom.common.bean.ApiResponse;
import com.ecom.common.dto.CartItem;
import com.ecom.common.dto.CartProduct;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/apiendpoint/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping("/get")
    public ResponseEntity<List<CartItem>> getCart(HttpServletRequest request) {
        return ResponseEntity.ok(cartService.getCart(request));
    }

    @PostMapping("/add")
    public ResponseEntity<ApiResponse> addProduct(HttpServletRequest request, @RequestBody CartProduct product) {
        ApiResponse res = cartService.addProductToCart(request, product);
        return ResponseEntity.status(res.getStatus()).body(res);
    }


    @PutMapping("/update/product/qty/{productId}")
    public ResponseEntity<ApiResponse> updateProductQty(HttpServletRequest request, @PathVariable Long productId, @RequestParam int qty) {
        ApiResponse res = cartService.updateProductQty(request, productId, qty);
        return ResponseEntity.status(res.getStatus()).body(res);
    }

    @PutMapping("/update/selectProduct/{productId}")
    public ResponseEntity<ApiResponse> updateSelectProduct(HttpServletRequest request, @PathVariable Long productId, @RequestParam boolean selected) {
        ApiResponse res = cartService.updateSelectProduct(request, productId, selected);
        return ResponseEntity.status(res.getStatus()).body(res);
    }


    @PutMapping("/update/selectAll")
    public ResponseEntity<ApiResponse> updateSelectAll(HttpServletRequest request,  @RequestParam boolean selected) {
        ApiResponse res = cartService.updateSelectAll(request, selected);
        return ResponseEntity.status(res.getStatus()).body(res);
    }


    @PutMapping("/update/selectCart/{created_by}")
    public ResponseEntity<ApiResponse> updateSelectCart(HttpServletRequest request, @PathVariable Long created_by, @RequestParam boolean selected) {
        ApiResponse res = cartService.updateSelectCart(request, created_by, selected);
        return ResponseEntity.status(res.getStatus()).body(res);
    }


    @DeleteMapping("/remove/{productId}")
    public ResponseEntity<ApiResponse> removeProduct(HttpServletRequest request, @PathVariable Long productId) {
        ApiResponse res = cartService.removeProductFromCart(request, productId);
        return ResponseEntity.status(res.getStatus()).body(res);
    }

    @DeleteMapping("/clear")
    public ResponseEntity<ApiResponse> clear(HttpServletRequest request) {
        ApiResponse res = cartService.clearCart(request);
        return ResponseEntity.status(res.getStatus()).body(res);
    }

    // CheckoutItems
    @GetMapping("/getCheckoutItems")
    public ResponseEntity<ApiResponse> getCheckoutItems(HttpServletRequest request){
        ApiResponse res = cartService.getCheckoutItems(request);
        return ResponseEntity.status(res.getStatus()).body(res);
    }

    @PutMapping("/removeCheckoutItems")
    public ResponseEntity<ApiResponse> removeCheckoutItemsFromCart(HttpServletRequest request){
        ApiResponse res = cartService.removeCheckoutItemsFromCart(request);
        return ResponseEntity.status(res.getStatus()).body(res);
    }

}
