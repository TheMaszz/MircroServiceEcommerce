package com.ecom.product_service.service;

import com.ecom.common.bean.ApiResponse;
import com.ecom.common.bean.ProductBean;
import com.ecom.common.bean.ProductImageBean;
import com.ecom.common.controller.BaseController;
import com.ecom.common.dto.ProductSearchDTO;
import com.ecom.common.dto.ShopDetailDTO;
import com.ecom.common.exception.BaseException;
import com.ecom.product_service.exception.ProductException;
import com.ecom.product_service.repository.ProductRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class ProductService extends BaseController {
    private final ProductRepository productRepository;
    private final FileStorageService fileStorageService;

    public ProductService(ProductRepository productRepository, FileStorageService fileStorageService) {
        this.productRepository = productRepository;
        this.fileStorageService = fileStorageService;
    }

    public ApiResponse getAllProduct(
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
            List<ProductBean> products = productRepository.findALlProduct(params);
            params.put("isCount", true);
            List<ProductBean> productsCount = productRepository.findALlProduct(params);
            res.getPaginate().setLimit(page_size);
            res.getPaginate().setPage(page_number);
            res.getPaginate().setTotal(productsCount.size());

            res.setData(products);
        } catch (Exception e) {
            this.checkException(e, res);
        }
        return res;
    }

    @Cacheable(value = "product", key = "#id", unless = "#result == null")
    public ApiResponse getProductById(Long id) throws BaseException {
        ApiResponse res = new ApiResponse();
        try {
            ProductBean product = productRepository.findProductById(id);
            if (product == null) {
                throw new ProductException("not.found", "product not found");
            }

            ShopDetailDTO shopDetailDTO = productRepository.findShopDetail(product.getCreated_by());
            if(shopDetailDTO == null){
                throw new ProductException("not.found.shop.detail", "shop detail not found");
            }

            Map<String, Object> result = new HashMap<>();
            result.put("product", product);
            result.put("shopDetail", shopDetailDTO);

            res.setData(result);
        } catch (Exception e) {
            this.checkException(e, res);
        }
        return res;
    }

    public ApiResponse createProduct(
            ProductBean productBean,
            List<MultipartFile> files,
            HttpServletRequest request
    ) throws BaseException {
        ApiResponse res = new ApiResponse();
        try {
            String userId = request.getHeader("X-User-Id");
            String role = request.getHeader("X-Role");
            productBean.setCreated_by(Long.valueOf(userId));

            Set<String> allowedRoles = Set.of("ADMIN", "SELLER");
            if (!allowedRoles.contains(role)) {
                throw new ProductException("no.permission.create", "you no permission to create");
            }

            if (files == null || files.isEmpty()) {
                throw new ProductException("image.at-least", "image at least 1");
            }

            productRepository.createProduct(productBean);

            List<ProductImageBean> productImages = new ArrayList<>();
            for (MultipartFile file : files) {
                String originFileName = file.getOriginalFilename();
                if (originFileName != null && !originFileName.isEmpty()) {
                    String fileExtension = originFileName.substring(originFileName.lastIndexOf("."));

                    String uniqueFileName = UUID.randomUUID().toString()
                            + "_"
                            + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
                            + fileExtension;

                    fileStorageService.saveProductImage(file, productBean.getId(), uniqueFileName);

                    ProductImageBean productImageBean = new ProductImageBean();
                    productImageBean.setProduct_id(productBean.getId());
                    productImageBean.setImage_url("/products/" + productBean.getId() + "/" + uniqueFileName);

                    productImages.add(productImageBean);
                }
            }

            productRepository.createProductImage(productImages);

        } catch (Exception e) {
            this.checkException(e, res);
        }
        return res;
    }

    public ApiResponse updateProduct(
            Long id, ProductBean productBean,
            List<MultipartFile> files,
            HttpServletRequest request
    ) throws BaseException {
        ApiResponse res = new ApiResponse();
        try {
            ProductBean product = productRepository.findProductById(id);
            if (product == null) {
                throw new ProductException("not.found", "product not found");
            }

            String userId = request.getHeader("X-User-Id");
            String role = request.getHeader("X-Role");
            productBean.setUpdated_by(Long.valueOf(userId));

            Set<String> allowedRoles = Set.of("ADMIN", "SELLER");
            if (!allowedRoles.contains(role)) {
                throw new ProductException("no.permission.update", "you no permission to update");
            }

            if ((files == null || files.isEmpty()) && productBean.getProduct_image().isEmpty()) {
                throw new ProductException("image.at-least", "image at least 1");
            }

            productBean.setId(id);

            List<ProductImageBean> originalProductImage = productRepository.findProductImageByProductId(id);
            List<ProductImageBean> newProductImage = productBean.getProduct_image();
            Map<Long, ProductImageBean> newProductImageMap = new HashMap<>();
            List<Long> toDeleteProductImage = new ArrayList<>();

            for (ProductImageBean newImage : newProductImage) {
                if (newImage.getId() != null) {
                    newProductImageMap.put(newImage.getId(), newImage);
                }
            }

            for (ProductImageBean originProductImage : originalProductImage) {
                ProductImageBean productImage = newProductImageMap.get(originProductImage.getId());
                if (productImage == null) {
                    toDeleteProductImage.add(originProductImage.getId());
                    fileStorageService.deleteFile(originProductImage.getImage_url());
                }
            }

            if (!toDeleteProductImage.isEmpty()) {
                productRepository.deleteProductImage(toDeleteProductImage);
            }

            if (files != null && !files.isEmpty()) {
                List<ProductImageBean> productImages = new ArrayList<>();
                for (MultipartFile file : files) {
                    String originFileName = file.getOriginalFilename();
                    if (originFileName != null && !originFileName.isEmpty()) {
                        String fileExtension = originFileName.substring(originFileName.lastIndexOf("."));

                        String uniqueFileName = UUID.randomUUID().toString()
                                + "_"
                                + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
                                + fileExtension;

                        fileStorageService.saveProductImage(file, productBean.getId(), uniqueFileName);

                        ProductImageBean productImageBean = new ProductImageBean();
                        productImageBean.setProduct_id(productBean.getId());
                        productImageBean.setImage_url("/products/" + productBean.getId() + "/" + uniqueFileName);

                        productImages.add(productImageBean);
                    }
                }

                productRepository.createProductImage(productImages);
            }


            productRepository.updateProduct(productBean);

        } catch (Exception e) {
            this.checkException(e, res);
        }
        return res;
    }

    public ApiResponse deleteProduct(
            Long id,
            HttpServletRequest request
    ) throws BaseException {
        ApiResponse res = new ApiResponse();
        try {
            ProductBean product = productRepository.findProductById(id);
            if (product == null) {
                throw new ProductException("not.found", "product not found");
            }

            String userId = request.getHeader("X-User-Id");
            String role = request.getHeader("X-Role");

            Set<String> allowedRoles = Set.of("ADMIN", "SELLER");
            if (!allowedRoles.contains(role)) {
                throw new ProductException("no.permission.delete", "you no permission to delete");
            }

            fileStorageService.deleteFolder("products", id);
            productRepository.deleteProduct(id);

        } catch (Exception e) {
            this.checkException(e, res);
        }
        return res;
    }

    public ApiResponse getAutoComplete(String search, Long limit) throws BaseException {
        ApiResponse res = new ApiResponse();
        HashMap<String, Object> params = new HashMap<>();
        try {
            params.put("search", search);
            if (limit != null) {
                params.put("limit", limit);
            }
            List<ProductSearchDTO> autoComplete = productRepository.findAutoCompleteProduct(params);
            res.setData(autoComplete);
        } catch (Exception e) {
            this.checkException(e, res);
        }
        return res;
    }

    public ApiResponse updateQty(Long id, Long qty) throws BaseException {
        ApiResponse res = new ApiResponse();
        try {
            ProductBean product = productRepository.findProductById(id);
            if (product == null) {
                throw new ProductException("not.found", "product not found");
            }

            Long currentQty = product.getQty() - qty;
            productRepository.updateQty(id, currentQty);
        } catch (Exception e) {
            this.checkException(e, res);
        }
        return res;
    }

    public ApiResponse getMyProducts(
            HttpServletRequest request, String search,
            int page_number,
            int page_size,
            String sort,
            String sort_type) throws BaseException {
        ApiResponse res = new ApiResponse();
        HashMap<String, Object> params = new HashMap<>();
        try {
            String userId = request.getHeader("X-User-Id");
            String role = request.getHeader("X-Role");

            Set<String> allowedRoles = Set.of("ADMIN", "SELLER");
            if (!allowedRoles.contains(role)) {
                throw new ProductException("no.permission.get.products", "you no permission to get products");
            }

            if (search != null && !search.isEmpty()) {
                params.put("search", search);
            }
            this.pagination(page_number, page_size, sort, sort_type, params);
            params.put("isCount", false);
            List<ProductBean> products = productRepository.findProductsByUserId(Long.valueOf(userId), params);
            params.put("isCount", true);
            List<ProductBean> productsCount = productRepository.findProductsByUserId(Long.valueOf(userId), params);
            res.getPaginate().setLimit(page_size);
            res.getPaginate().setPage(page_number);
            res.getPaginate().setTotal(productsCount.size());

            res.setData(products);
        } catch (Exception e) {
            this.checkException(e, res);
        }
        return res;
    }

    public ApiResponse getShopProductsByUserId(
            Long userId,
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
            params.put("userId", userId);
            this.pagination(page_number, page_size, sort, sort_type, params);
            params.put("isCount", false);
            List<ProductBean> products = productRepository.findShopProducts(params);
            params.put("isCount", true);
            List<ProductBean> productsCount = productRepository.findShopProducts(params);
            res.getPaginate().setLimit(page_size);
            res.getPaginate().setPage(page_number);
            res.getPaginate().setTotal(productsCount.size());

            ShopDetailDTO shopDetailDTO = productRepository.findShopDetail(userId);
            if(shopDetailDTO == null){
                throw new ProductException("not.found.shop.detail", "shop detail not found");
            }

            Map<String, Object> result = new HashMap<>();
            result.put("products", products);
            result.put("shopDetail", shopDetailDTO);

            res.setData(result);
        } catch (Exception e) {
            this.checkException(e, res);
        }
        return res;
    }

}
