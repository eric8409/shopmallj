package com.eric.shopmall.rowmapper;

import com.eric.shopmall.constant.ProductCategory;
import com.eric.shopmall.model.Product;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ProductRowMapper implements RowMapper<Product> {

    @Override
    public Product mapRow(ResultSet resultSet, int i) throws SQLException {

     Product product = new Product();

     product.setProduct_Id(resultSet.getInt("product_id"));
     product.setProduct_Name(resultSet.getString("product_name"));

     String categoryStr = resultSet.getString("category");
     ProductCategory category = ProductCategory.valueOf(categoryStr);
     product.setCategory(category);


     product.setImage_url(resultSet.getString("image_url"));
     product.setPrice(resultSet.getInt("price"));
     product.setStock(resultSet.getInt("stock"));
     product.setDescription(resultSet.getString("description"));
     product.setCreate_date(resultSet.getTimestamp("created_date"));
     product.setLast_modified_date(resultSet.getTimestamp("last_modified_date"));

     return product;
    }
}
