package com.ecom.product_service.repository;

import com.ecom.product_service.bean.ProductBean;
import com.ecom.product_service.bean.ProductImageBean;
import com.ecom.product_service.exeption.BaseException;
import org.apache.ibatis.annotations.*;

import java.util.HashMap;
import java.util.List;

@Mapper
public interface ProductRepository {

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
            "WHERE 1=1",
            "<if test='search != null and search != \"\"'>",
            " AND p.name LIKE CONCAT('%', #{search}, '%')",
            "</if>",
            "<if test='sorting != null and sorting != \"\" and sort_type != null and sort_type != \"\"'>",
            " ORDER BY ${sorting} ${sort_type}",
            "</if>",
            "LIMIT #{start}, #{end}",
            "</script>"
    })
    @Results(
            id = "ProductResultMap",
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
    List<ProductBean> findALlProduct(HashMap<String, Object> params) throws BaseException;

    @Select("SELECT COUNT(*) FROM product")
    public int findCountProduct() throws BaseException;

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

    @Insert({
            "INSERT INTO product (name, description, qty, price, created_at, created_by)",
            "VALUES (#{name}, #{description}, #{qty}, #{price}, now(), #{created_by})"
    })
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    public void createProduct(ProductBean productBean) throws BaseException;

    @Update({
            "UPDATE product SET",
            "name = #{name},",
            "description = #{description},",
            "qty = #{qty},",
            "updated_at = now(),",
            "updated_by = #{updated_by}",
            "WHERE id = #{id}"
    })
    public void updateProduct(ProductBean productBean) throws BaseException;

    @Delete("DELETE FROM product where id = #{id}")
    public void deleteProduct(Long id);

    @Insert({
            "<script>",
            "INSERT INTO product_image (product_id, image_url)",
            "VALUES ",
            "<foreach collection='productImages' item='image' separator=','>",
            "   (#{image.product_id}, #{image.image_url})",
            "</foreach>",
            "</script>"
    })
    public void createProductImage(List<ProductImageBean> productImages) throws BaseException;

    @Update({
            "<script>",
            "UPDATE product_image",
            "<set>",
            "  image_url = CASE",
            "  <foreach collection='productImages' item='item'>",
            "    WHEN id = #{item.id} THEN #{item.imageUrl}",
            "  </foreach>",
            "  END,",
            "  updated_at = NOW()",
            "</set>",
            "WHERE id IN",
            "  <foreach collection='productImages' item='item' open='(' separator=',' close=')'>",
            "    #{item.id}",
            "  </foreach>",
            "</script>"
    })
    public void updateProductImage(@Param("productImages") List<ProductImageBean> productImages) throws BaseException;

    @Delete({
            "<script>",
            "DELETE FROM product_image",
            "WHERE id IN",
            "<foreach collection='ids' item='id' open='(' separator=',' close=')'>",
            "   #{id}",
            "</foreach>",
            "</script>"
    })
    public void deleteProductImage(@Param("ids") List<Long> ids) throws BaseException;

    @Select({
            "SELECT * FROM product_image WHERE product_id = #{product_id}"
    })
    public List<ProductImageBean> findProductImageByProductId(Long productId) throws BaseException;


}
