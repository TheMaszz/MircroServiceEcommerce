package com.ecom.cart_service.service;

import com.ecom.common.bean.ApiResponse;
import com.ecom.common.dto.CartItem;
import com.ecom.common.dto.CartProduct;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CartService {

    private final RedisTemplate<String, Object> redisTemplate;

    public CartService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    private String getKey(Long userId) {
        return "cart:user:" + userId;
    }

    public List<CartItem> getCart(HttpServletRequest request) {
        String userId = request.getHeader("X-User-Id");

        Object data = redisTemplate.opsForValue().get(getKey(Long.valueOf(userId)));
        if (data != null) {
            return (List<CartItem>) data;
        }
        return new ArrayList<>();
    }

    public void saveCart(Long userId, List<CartItem> cart) {
        redisTemplate.opsForValue().set(getKey(userId), cart);
    }

    public ApiResponse addProductToCart(HttpServletRequest request, CartProduct product) {
        ApiResponse res = new ApiResponse();
        try {
            String userId = request.getHeader("X-User-Id");
            List<CartItem> cart = getCart(request);

            CartItem existingCart = cart.stream()
                    .filter(c -> c.getCreated_by().equals(product.getCreated_by()))
                    .findFirst()
                    .orElse(null);

            if (existingCart != null) {
                Optional<CartProduct> existingProduct = existingCart.getProducts().stream()
                        .filter(p -> p.getId().equals(product.getId()))
                        .findFirst();

                if (existingProduct.isPresent()) {
                    existingProduct.get().setQty(existingProduct.get().getQty() + product.getQty());
                    existingProduct.get().setTotalPrice(existingProduct.get().getQty() * product.getPrice());
                } else {
                    product.setTotalPrice(product.getQty() * product.getPrice());
                    existingCart.getProducts().add(product);
                }
            } else {
                CartItem newCart = new CartItem();
                newCart.setCreated_by(product.getCreated_by());
                newCart.setCreated_user(product.getCreated_user());
                newCart.setSelected(false);
                product.setTotalPrice(product.getQty() * product.getPrice());
                newCart.setProducts(new ArrayList<>(List.of(product)));
                cart.add(newCart);
            }

            saveCart(Long.valueOf(userId), cart);
            res.setResponse_desc("Added to cart");
            res.setData(cart);
        } catch (Exception e) {
            throw new RuntimeException("Error while adding product to cart", e);
        }
        return res;
    }

    public ApiResponse updateProductQty(HttpServletRequest request, Long productId, int qty) {
        ApiResponse res = new ApiResponse();
        try {
            String userId = request.getHeader("X-User-Id");
            List<CartItem> cart = getCart(request);

            for (CartItem item : cart) {
                Optional<CartProduct> existingProduct = item.getProducts().stream()
                        .filter(p -> p.getId().equals(productId))
                        .findFirst();

                if (existingProduct.isPresent()) {
                    existingProduct.get().setQty(qty);
                    existingProduct.get().setTotalPrice(qty * existingProduct.get().getPrice());
                }
            }

            saveCart(Long.valueOf(userId), cart);
            res.setResponse_desc("Updated quantity");
            res.setData(cart);
        } catch (Exception e) {
            throw new RuntimeException("Error while updating product quantity", e);
        }
        return res;
    }

    public ApiResponse updateSelectProduct(HttpServletRequest request, Long productId, boolean selected) {
        ApiResponse res = new ApiResponse();
        try {
            String userId = request.getHeader("X-User-Id");
            List<CartItem> cart = getCart(request);

            cart.forEach(item -> {
                item.getProducts().stream()
                        .filter(p -> p.getId().equals(productId))
                        .findFirst()
                        .ifPresent(product -> {
                            product.setSelected(selected);
                            item.setSelected(updateParentSelectionStatus(item));
                        });
            });

            saveCart(Long.valueOf(userId), cart);
            res.setResponse_desc("Updated product selection");
            res.setData(cart);
        } catch (Exception e) {
            throw new RuntimeException("Error while updating select product", e);
        }
        return res;
    }

    public ApiResponse updateSelectAll(HttpServletRequest request, boolean selected) {
        ApiResponse res = new ApiResponse();
        try {
            String userId = request.getHeader("X-User-Id");
            List<CartItem> cart = getCart(request);

            for (CartItem item : cart) {
                item.setSelected(selected);
                for (CartProduct product : item.getProducts()) {
                    product.setSelected(selected);
                }
            }

            saveCart(Long.valueOf(userId), cart);
            res.setResponse_desc("Updated all selections");
            res.setData(cart);
        } catch (Exception e) {
            throw new RuntimeException("Error while updating select all", e);
        }
        return res;
    }

    public ApiResponse updateSelectCart(HttpServletRequest request, Long created_by, boolean selected) {
        ApiResponse res = new ApiResponse();
        try {
            String userId = request.getHeader("X-User-Id");
            List<CartItem> cart = getCart(request);

            for (CartItem item : cart) {
                if (item.getCreated_by().equals(created_by)) {
                    item.setSelected(selected);
                    for (CartProduct product : item.getProducts()) {
                        product.setSelected(selected);
                    }
                }
            }

            saveCart(Long.valueOf(userId), cart);
            res.setResponse_desc("Updated cart selections");
            res.setData(cart);
        } catch (Exception e) {
            throw new RuntimeException("Error while updating select cart", e);
        }
        return res;
    }

    public ApiResponse removeProductFromCart(HttpServletRequest request, Long productId) {
        ApiResponse res = new ApiResponse();
        try {
            String userId = request.getHeader("X-User-Id");
            List<CartItem> cart = getCart(request);

            cart.removeIf(item -> {
                boolean removed = item.getProducts().removeIf(p -> p.getId().equals(productId));
                return removed && (item.getProducts() == null || item.getProducts().isEmpty());
            });

            if (cart.isEmpty()) {
                redisTemplate.delete(getKey(Long.valueOf(userId)));
                res.setResponse_desc("removed product from cart and all clean");
                res.setData(new ArrayList<>());
            } else {
                saveCart(Long.valueOf(userId), cart);
                res.setResponse_desc("removed product from cart");
                res.setData(cart);
            }

        } catch (Exception e) {
            throw new RuntimeException("Error while remove product from cart", e);
        }
        return res;
    }

    public ApiResponse clearCart(HttpServletRequest request) {
        ApiResponse res = new ApiResponse();
        try {
            String userId = request.getHeader("X-User-Id");

            redisTemplate.delete(getKey(Long.valueOf(userId)));

            res.setResponse_desc("clear cart");
            res.setData(new ArrayList<>());
        } catch (Exception e) {
            throw new RuntimeException("Error while clear cart", e);
        }
        return res;
    }

    public ApiResponse getCheckoutItems(HttpServletRequest request) {
        ApiResponse res = new ApiResponse();
        try {
            String userId = request.getHeader("X-User-Id");
            List<CartItem> cart = getCart(request);

            List<CartItem> checkoutItems = cart.stream()
                    .filter(item -> item.getProducts() != null &&
                            item.getProducts().stream()
                                    .anyMatch(p -> p.getSelected() != null && p.getSelected()))
                    .map(item -> {
                        CartItem filteredItem = new CartItem();
                        filteredItem.setCreated_by(item.getCreated_by());
                        filteredItem.setCreated_user(item.getCreated_user());
                        filteredItem.setSelected(item.getSelected());

                        List<CartProduct> selectedProducts = item.getProducts().stream()
                                .filter(p -> p.getSelected() != null && p.getSelected())
                                .map(p -> {
                                    CartProduct product = new CartProduct();
                                    product.setId(p.getId());
                                    product.setName(p.getName());
                                    product.setPrice(p.getPrice());
                                    product.setQty(p.getQty());
                                    product.setQtyInStock(p.getQtyInStock());
                                    product.setImageUrl(p.getImageUrl());
                                    product.setCreated_by(p.getCreated_by());
                                    product.setCreated_user(p.getCreated_user());
                                    product.setSelected(true);
                                    product.setTotalPrice(
                                            p.getTotalPrice() != null ? p.getTotalPrice() : p.getPrice() * p.getQty()
                                    );
                                    return product;
                                })
                                .collect(Collectors.toList());

                        filteredItem.setProducts(selectedProducts);
                        return filteredItem;
                    })
                    .collect(Collectors.toList());

            res.setResponse_desc("Selected checkout items retrieved");
            res.setData(checkoutItems);
        } catch (Exception e) {
            throw new RuntimeException("Error while selected checkout items retrieved", e);

        }
        return res;
    }

    public ApiResponse removeCheckoutItemsFromCart(HttpServletRequest request) {
        ApiResponse res = new ApiResponse();
        try {
            String userId = request.getHeader("X-User-Id");
            List<CartItem> cart = getCart(request);

            List<CartItem> updatedCart = cart.stream()
                    .filter(item -> item.getSelected() == null || !item.getSelected())
                    .peek(item -> {
                        if (item.getProducts() != null) {
                            item.getProducts().removeIf(p -> p.getSelected() != null && p.getSelected());
                        }
                    })
                    .filter(item -> item.getProducts() != null && !item.getProducts().isEmpty())
                    .collect(Collectors.toList());

            if (updatedCart.isEmpty()) {
                redisTemplate.delete(getKey(Long.valueOf(userId)));
                res.setResponse_desc("All items removed, cart is now empty");
                res.setData(new ArrayList<>());
            } else {
                List<CartItem> responseData = new ArrayList<>(updatedCart);
                saveCart(Long.valueOf(userId), responseData);
                res.setResponse_desc("Selected items removed successfully");
                res.setData(responseData);
            }

        } catch (Exception e) {
            throw new RuntimeException("Error while removing checkout items from cart", e);
        }
        return res;
    }

    private boolean updateParentSelectionStatus(CartItem item) {
        return item.getProducts().stream()
                .allMatch(CartProduct::getSelected);
    }

}
