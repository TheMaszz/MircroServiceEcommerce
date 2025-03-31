package com.ecom.order_service.repository;

import com.ecom.common.bean.OrderBean;
import com.ecom.common.bean.PaymentStatusBean;
import com.ecom.common.bean.ProductBean;
import com.ecom.common.bean.ProductImageBean;
import com.ecom.common.exception.BaseException;
import org.apache.ibatis.annotations.*;

import java.util.HashMap;
import java.util.List;

@Mapper
public interface OrderRepository {

    @Select({
            "<script>",
            "SELECT",
            " o.id,",
            " o.user_id,",
            " o.address_id,",
            " o.stage,",
            " o.total_amount,",
            " o.created_at,",
            " o.updated_at,",
            " u.username",
            "FROM order_master AS o",
            "LEFT JOIN user AS u ON o.user_id = u.id",
            "WHERE o.user_id = #{user_id}",
            "<if test='search != null and search != \"\"'>",
            " AND o.id LIKE CONCAT('%', #{search}, '%')",
            "</if>",
            "<if test='isCount == false'>",
            " Order by ${sorting} ${sort_type} limit #{start}, #{end}",
            "</if>",
            "</script>"
    })
    @Results(
            id = "MyOrdersMap",
            value = {
                    @Result(property = "id", column = "id"),
                    @Result(property = "user_id", column = "user_id"),
                    @Result(property = "address_id", column = "address_id"),
                    @Result(property = "stage", column = "stage"),
                    @Result(property = "total_amount", column = "total_amount"),
                    @Result(property = "created_at", column = "created_at"),
                    @Result(property = "updated_at", column = "updated_at"),
                    @Result(property = "products", column = "id",
                            many = @Many(select = "findOrderProductByOrderId"))
            }
    )
    public List<OrderBean> findMyOrders(HashMap<String, Object> params) throws BaseException;

    @Select({
            "SELECT * FROM order_product WHERE order_id = #{id}"
    })
    @Results(
            id = "OrderProductMap",
            value = {
                    @Result(property = "id", column = "id"),
                    @Result(property = "order_id", column = "order_id"),
                    @Result(property = "product_id", column = "product_id"),
                    @Result(property = "productDetail", column = "product_id",
                            many = @Many(select = "findProductById"))
            }
    )
    public List<OrderBean.OrderProduct> findOrderProductByOrderId(Long id) throws BaseException;

    @Select({
            "<script>",
            "SELECT",
            " p.id,",
            " p.name,",
            " p.description,",
            " p.qty,",
            " p.price,",
            " p.created_at,",
            " p.updated_at,",
            " p.created_by,",
            " p.updated_by,",
            " u.username AS create_user",
            "FROM product AS p",
            "LEFT JOIN user AS u ON p.created_by = u.id",
            "WHERE p.id = #{id}",
            "</script>"
    })
    @Results(
            id = "ProductIdResultMap",
            value = {
                    @Result(property = "id", column = "id"),
                    @Result(property = "name", column = "name"),
                    @Result(property = "description", column = "description"),
                    @Result(property = "qty", column = "qty"),
                    @Result(property = "price", column = "price"),
                    @Result(property = "created_at", column = "created_at"),
                    @Result(property = "updated_at", column = "updated_at"),
                    @Result(property = "created_by", column = "created_by"),
                    @Result(property = "updated_by", column = "updated_by"),
                    @Result(property = "product_image", column = "id",
                            many = @Many(select = "findProductImageByProductId"))
            }
    )
    public ProductBean findProductById(Long id) throws BaseException;

    @Select({
            "SELECT * FROM product_image WHERE product_id = #{product_id}"
    })
    public List<ProductImageBean> findProductImageByProductId(Long productId) throws BaseException;

    @Select({
            "<script>",
            "SELECT",
            " o.id,",
            " o.user_id,",
            " o.address_id,",
            " o.stage,",
            " o.total_amount,",
            " o.created_at,",
            " o.updated_at,",
            " u.username",
            "FROM order_master AS o",
            "LEFT JOIN user AS u ON o.user_id = u.id",
            "WHERE 1=1",
            "<if test='search != null and search != \"\"'>",
            " AND o.id LIKE CONCAT('%', #{search}, '%')",
            "</if>",
            "<if test='isCount == false'>",
            " Order by ${sorting} ${sort_type} limit #{start}, #{end}",
            "</if>",
            "</script>"
    })
    @Results(
            id = "AllOrdersMap",
            value = {
                    @Result(property = "id", column = "id"),
                    @Result(property = "user_id", column = "user_id"),
                    @Result(property = "address_id", column = "address_id"),
                    @Result(property = "stage", column = "stage"),
                    @Result(property = "total_amount", column = "total_amount"),
                    @Result(property = "created_at", column = "created_at"),
                    @Result(property = "updated_at", column = "updated_at"),
                    @Result(property = "products", column = "id",
                            many = @Many(select = "findOrderProductByOrderId"))
            }
    )
    public List<OrderBean> findAll(HashMap<String, Object> params) throws BaseException;

    @Select({
            "<script>",
            "SELECT",
            " o.id,",
            " o.user_id,",
            " o.address_id,",
            " o.stage,",
            " o.total_amount,",
            " o.created_at,",
            " o.updated_at,",
            " u.username",
            "FROM order_master AS o",
            "LEFT JOIN user AS u ON o.user_id = u.id",
            "WHERE o.id = #{id}",
            "</script>"
    })
    @Results(
            id = "OrderByIdMap",
            value = {
                    @Result(property = "id", column = "id"),
                    @Result(property = "user_id", column = "user_id"),
                    @Result(property = "address_id", column = "address_id"),
                    @Result(property = "stage", column = "stage"),
                    @Result(property = "total_amount", column = "total_amount"),
                    @Result(property = "created_at", column = "created_at"),
                    @Result(property = "updated_at", column = "updated_at"),
                    @Result(property = "products", column = "id",
                            many = @Many(select = "findOrderProductByOrderId"))
            }
    )
    public OrderBean findById(Long id) throws BaseException;

    @Insert({
            "<script>",
            "INSERT INTO order_master (user_id, address_id, stage, total_amount, created_at)",
            "VALUES",
            "<foreach collection='orderBeanList' item='order' separator=','>",
            "(#{order.user_id}, #{order.address_id}, #{order.stage}, #{order.total_amount}, now())",
            "</foreach>",
            "</script>"
    })
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    void createOrder(@Param("orderBeanList") List<OrderBean> orderBeanList) throws BaseException;

    @Insert({
            "INSERT INTO payment_status (status, order_id) VALUES ('Unpaid', #{order_id})"
    })
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    void createPaymentStatus(PaymentStatusBean paymentStatusBean);

    @Insert({
            "<script>",
            "INSERT INTO order_product",
            "(order_id, product_id)",
            "VALUES",
            "<foreach collection='orderProduct' item='op' separator=','>",
            " (#{op.order_id}, #{op.product_id})",
            "</foreach>",
            "</script>"
    })
    public void createOrderProduct(List<OrderBean.OrderProduct> orderProduct);

    @Update({
            "UPDATE order_master SET",
            "stage = #{stage},",
            "updated_at = now()",
            "WHERE id = #{id}"
    })
    public void updateOrder(OrderBean orderBean) throws BaseException;

    @Update({
            "UPDATE order_master SET",
            "stage = #{stage},",
            "updated_at = now()",
            "WHERE id = #{id}"
    })
    public void updateStage(@Param("id") Long id, @Param("stage") String stage) throws BaseException;

    @Update({
            "UPDATE payment_status SET",
            "status = #{status},",
            "stripe_session_id = #{stripe_session_id},",
            "stripe_checkout_url = #{stripe_checkout_url}",
            "WHERE id = #{id}"
    })
    public void updatePaymentStatus(PaymentStatusBean paymentStatusBean) throws BaseException;

    @Select({
            "SELECT om.id, ps.id AS payment_status_id ",
            "FROM payment_status AS ps",
            "JOIN order_master AS om ON ps.order_id = om.id",
            "WHERE ps.stripe_session_id = #{sessionId}"
    })
    public List<OrderBean> findBySessionId(String sessionId) throws BaseException;

    @Select({
            "SELECT * FROM payment_status WHERE id = #{id}"
    })
    public PaymentStatusBean findPaymentStatusById(Long id) throws BaseException;

}
