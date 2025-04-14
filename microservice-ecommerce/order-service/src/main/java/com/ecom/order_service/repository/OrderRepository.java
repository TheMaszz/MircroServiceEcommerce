package com.ecom.order_service.repository;

import com.ecom.common.bean.OrderBean;
import com.ecom.common.bean.PaymentStatusBean;
import com.ecom.common.bean.ProductBean;
import com.ecom.common.bean.ProductImageBean;
import com.ecom.common.dto.OrderPaymentDTO;
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
            " o.shop_id,",
            " o.stage,",
            " o.total_amount,",
            " o.created_at,",
            " o.updated_at,",
            " u.username,",
            " u2.username AS shop_name",
            "FROM order_master AS o",
            "LEFT JOIN user AS u ON o.user_id = u.id",
            "LEFT JOIN user AS u2 ON o.shop_id = u2.id",
            "WHERE 1=1",

            "<if test='user_id != null'>",
            "AND o.user_id = #{user_id}",
            "</if>",

            "<if test='shop_id != null'>",
            "AND o.shop_id = #{shop_id}",
            "</if>",

            "<if test='search != null and search != \"\"'>",
            " AND o.id LIKE CONCAT('%', #{search}, '%')",
            "</if>",

            "<if test='stageList != null and stageList.size() > 0'>",
            " AND o.stage IN ",
            "<foreach item='stg' collection='stageList' open='(' separator=',' close=')'>",
            " #{stg} ",
            "</foreach>",
            "</if>",

            "<if test='stage != \"All\" and (stageList == null or stageList.size() == 0)'>",
            " AND o.stage = #{stage}",
            "</if>",

            "<if test=\"start_date != null and end_date != null\">",
            " AND o.created_at &gt;= #{start_date} AND o.created_at &lt; DATE_ADD(#{end_date}, INTERVAL 1 DAY)",
            "</if>",

            "<if test='isCount == false'>",
            " ORDER BY ${sorting} ${sort_type} LIMIT #{start}, #{end}",
            "</if>",
            "</script>"
    })
    @Results(
            id = "MyOrdersMap",
            value = {
                    @Result(property = "id", column = "id"),
                    @Result(property = "user_id", column = "user_id"),
                    @Result(property = "address_id", column = "address_id"),
                    @Result(property = "shop_id", column = "shop_id"),
                    @Result(property = "stage", column = "stage"),
                    @Result(property = "total_amount", column = "total_amount"),
                    @Result(property = "created_at", column = "created_at"),
                    @Result(property = "updated_at", column = "updated_at"),
                    @Result(property = "shop_name", column = "shop_name"),
                    @Result(property = "products", column = "id",
                            many = @Many(select = "findOrderProductByOrderId")),
                    @Result(property = "paymentStatus", column = "id",
                            many = @Many(select = "findPaymentStatusByOrderId"))
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
                    @Result(property = "qty", column = "qty"),
                    @Result(property = "productDetail", column = "product_id",
                            many = @Many(select = "findProductById"))
            }
    )
    public List<OrderBean.OrderProduct> findOrderProductByOrderId(Long id) throws BaseException;

    @Select({
            "SELECT * FROM payment_status WHERE order_id = #{id}"
    })
    public PaymentStatusBean findPaymentStatusByOrderId(Long id) throws BaseException;

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
            " o.shop_id,",
            " o.stage,",
            " o.total_amount,",
            " o.created_at,",
            " o.updated_at,",
            " u.username,",
            " u2.username AS shop_name",
            "FROM order_master AS o",
            "LEFT JOIN user AS u ON o.user_id = u.id",
            "LEFT JOIN user AS u2 ON o.shop_id = u2.id",
            "WHERE 1=1",

            "<if test='search != null and search != \"\"'>",
            " AND o.id LIKE CONCAT('%', #{search}, '%')",
            "</if>",

            "<if test='stageList != null and stageList.size() > 0'>",
            " AND o.stage IN ",
            "<foreach item='stg' collection='stageList' open='(' separator=',' close=')'>",
            " #{stg} ",
            "</foreach>",
            "</if>",

            "<if test='stage != \"All\" and (stageList == null or stageList.size() == 0)'>",
            " AND o.stage = #{stage}",
            "</if>",

            "<if test=\"start_date != null and end_date != null\">",
            " AND o.created_at &gt;= #{start_date} AND o.created_at &lt; DATE_ADD(#{end_date}, INTERVAL 1 DAY)",
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
                    @Result(property = "shop_name", column = "shop_name"),
                    @Result(property = "products", column = "id",
                            many = @Many(select = "findOrderProductByOrderId")),
                    @Result(property = "paymentStatus", column = "id",
                            many = @Many(select = "findPaymentStatusByOrderId"))
            }
    )
    public List<OrderBean> findAll(HashMap<String, Object> params) throws BaseException;

    @Select({
            "<script>",
            "SELECT",
            " o.id,",
            " o.user_id,",
            " o.address_id,",
            " o.shop_id,",
            " o.stage,",
            " o.total_amount,",
            " o.created_at,",
            " o.updated_at,",
            " u.username,",
            " u2.username AS shop_name",
            "FROM order_master AS o",
            "LEFT JOIN user AS u ON o.user_id = u.id",
            "LEFT JOIN user AS u2 ON o.shop_id = u2.id",
            "WHERE o.id = #{id}",
            "</script>"
    })
    @Results(
            id = "OrderByIdMap",
            value = {
                    @Result(property = "id", column = "id"),
                    @Result(property = "user_id", column = "user_id"),
                    @Result(property = "address_id", column = "address_id"),
                    @Result(property = "shop_id", column = "shop_id"),
                    @Result(property = "stage", column = "stage"),
                    @Result(property = "total_amount", column = "total_amount"),
                    @Result(property = "created_at", column = "created_at"),
                    @Result(property = "updated_at", column = "updated_at"),
                    @Result(property = "shop_name", column = "shop_name"),
                    @Result(property = "products", column = "id",
                            many = @Many(select = "findOrderProductByOrderId"))
            }
    )
    public OrderBean findById(Long id) throws BaseException;

    @Insert({
            "<script>",
            "INSERT INTO order_master (user_id, address_id, shop_id, stage, total_amount, created_at)",
            "VALUES",
            "<foreach collection='orderBeanList' item='order' separator=','>",
            "(#{order.user_id}, #{order.address_id}, #{order.shop_id}, #{order.stage}, #{order.total_amount}, now())",
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
            "(order_id, product_id, qty)",
            "VALUES",
            "<foreach collection='orderProduct' item='op' separator=','>",
            " (#{op.order_id}, #{op.product_id}, #{op.qty})",
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
            "SELECT om.id AS orderId, ps.id AS paymentStatusId",
            "FROM payment_status AS ps",
            "JOIN order_master AS om ON ps.order_id = om.id",
            "WHERE ps.stripe_session_id = #{sessionId}"
    })
    @Results(
            id = "OrderPaymentBySession",
            value = {
                    @Result(property = "orderId", column = "orderId"),
                    @Result(property = "paymentStatusId", column = "paymentStatusId"),
                    @Result(property = "products", column = "orderId",
                            many = @Many(select = "findOrderProductByOrderId"))
            }
    )
    public List<OrderPaymentDTO> findBySessionId(String sessionId) throws BaseException;

    @Select({
            "SELECT * FROM payment_status WHERE id = #{id}"
    })
    public PaymentStatusBean findPaymentStatusById(Long id) throws BaseException;

}
