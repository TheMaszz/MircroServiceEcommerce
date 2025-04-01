package com.ecom.payment_service.client;

import com.ecom.common.bean.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "product-service")
public interface ProductClient {

    @PutMapping("/apiendpoint/product/updateQty/{id}")
    ApiResponse updateQty(@PathVariable Long id, @RequestBody Long qty);

}
