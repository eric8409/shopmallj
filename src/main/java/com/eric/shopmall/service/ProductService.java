package com.eric.shopmall.service;

import com.eric.shopmall.constant.ProductCategory;
import com.eric.shopmall.dto.ProductQueryParams;
import com.eric.shopmall.dto.ProductRequest;
import com.eric.shopmall.model.Product;

import java.util.List;

public interface ProductService {

    Integer countProduct(ProductQueryParams productQueryParams);

    List<Product> getProducts(ProductQueryParams productQueryParams);

    Product getProductById(Integer product_Id);

    Integer createProduct(ProductRequest productRequest);

    void updateProduct(Integer productId, ProductRequest productRequest);

    void deleteProductById(Integer productId);

}
