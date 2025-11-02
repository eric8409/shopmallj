package com.eric.shopmall.dao;

import com.eric.shopmall.constant.ProductCategory;
import com.eric.shopmall.dto.ProductQueryParams;
import com.eric.shopmall.dto.ProductRequest;
import com.eric.shopmall.model.Product;

import java.util.List;

public interface ProductDao {

    Integer countProduct(ProductQueryParams productQueryParams);

    List<Product> getProducts(ProductQueryParams productQueryParams);

    Product getProductById(Integer product_Id);

    Integer createProduct(ProductRequest productRequest);

    void updateProduct(Integer productId, ProductRequest productRequest);

    void updateStock(Integer productId, Integer stock);

    void deleteProductById(Integer productId);
}
