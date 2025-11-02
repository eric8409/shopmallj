//package com.eric.shopmall.controller;
//
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.eric.shopmall.constant.ProductCategory;
//import com.eric.shopmall.dto.ProductRequest;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.http.MediaType;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.RequestBuilder;
//import org.springframework.test.web.servlet.ResultActions;
//import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
//import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
//import org.springframework.transaction.annotation.Transactional;
//
//import static org.hamcrest.Matchers.*;
//import static org.junit.jupiter.api.Assertions.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@SpringBootTest
//@AutoConfigureMockMvc
//public class ProductControllerTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    private ObjectMapper objectMapper = new ObjectMapper();
//
//    // 查詢商品
//    @Test
//    public void getProduct_success() throws Exception {
//        RequestBuilder requestBuilder = MockMvcRequestBuilders
//                .get("/products/{productId}", 1);
//
//        mockMvc.perform(requestBuilder)
//                .andDo(print())
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.product_Name", equalTo("蘋果（澳洲）")))
//                .andExpect(jsonPath("$.category", equalTo("FOOD")))
//                .andExpect(jsonPath("$.image_url", notNullValue()))
//                .andExpect(jsonPath("$.price", notNullValue()))
//                .andExpect(jsonPath("$.stock", notNullValue()))
//                .andExpect(jsonPath("$.description", notNullValue()))
//                .andExpect(jsonPath("$.create_date", notNullValue()))
//                .andExpect(jsonPath("$.last_modified_date", notNullValue()));
//    }
//
//    @Test
//    public void getProduct_notFound() throws Exception {
//        RequestBuilder requestBuilder = MockMvcRequestBuilders
//                .get("/products/{productId}", 20000);
//
//        mockMvc.perform(requestBuilder)
//                .andExpect(status().is(404));
//    }
//
//    // 創建商品
//    @Transactional
//    @Test
//    public void createProduct_success() throws Exception {
//        ProductRequest productRequest = new ProductRequest();
//        productRequest.setProduct_Name("test food product");
//        productRequest.setCategory(ProductCategory.FOOD);
//        productRequest.setImage_url("http://test.com");
//        productRequest.setPrice(100);
//        productRequest.setStock(2);
//
//        String json = objectMapper.writeValueAsString(productRequest);
//
//        RequestBuilder requestBuilder = MockMvcRequestBuilders
//                .post("/products")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(json);
//
//        mockMvc.perform(requestBuilder)
//                .andExpect(status().is(201))
//                .andExpect(jsonPath("$.product_Name", equalTo("test food product")))
//                .andExpect(jsonPath("$.category", equalTo("FOOD")))
//                .andExpect(jsonPath("$.image_url", equalTo("http://test.com")))
//                .andExpect(jsonPath("$.price", equalTo(100)))
//                .andExpect(jsonPath("$.stock", equalTo(2)))
//                .andExpect(jsonPath("$.description", nullValue()))
//                .andExpect(jsonPath("$.create_date", notNullValue()))
//                .andExpect(jsonPath("$.last_modified_date", notNullValue()));
//    }
//
//    @Transactional
//    @Test
//    public void createProduct_illegalArgument() throws Exception {   //少傳參數
//        ProductRequest productRequest = new ProductRequest();
//        productRequest.setProduct_Name("test food product");
//
//        String json = objectMapper.writeValueAsString(productRequest);
//
//        RequestBuilder requestBuilder = MockMvcRequestBuilders
//                .post("/products")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(json);
//
//        mockMvc.perform(requestBuilder)
//                .andExpect(status().is(400));
//    }
//
//    // 更新商品
//    @Transactional
//    @Test
//    public void updateProduct_success() throws Exception {
//        ProductRequest productRequest = new ProductRequest();
//        productRequest.setProduct_Name("test food product");
//        productRequest.setCategory(ProductCategory.FOOD);
//        productRequest.setImage_url("http://test.com");
//        productRequest.setPrice(100);
//        productRequest.setStock(2);
//
//        String json = objectMapper.writeValueAsString(productRequest);
//
//        RequestBuilder requestBuilder = MockMvcRequestBuilders
//                .put("/products/{productId}", 3)
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(json);
//
//
//        mockMvc.perform(requestBuilder)
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.product_Name", equalTo("test food product")))
//                .andExpect(jsonPath("$.category", equalTo("FOOD")))
//                .andExpect(jsonPath("$.image_url", equalTo("http://test.com")))
//                .andExpect(jsonPath("$.price", equalTo(100)))
//                .andExpect(jsonPath("$.stock", equalTo(2)))
//                .andExpect(jsonPath("$.description", nullValue()))
//                .andExpect(jsonPath("$.create_date", notNullValue()))
//                .andExpect(jsonPath("$.last_modified_date", notNullValue()));
//    }
//
//    @Transactional
//    @Test
//    public void updateProduct_illegalArgument() throws Exception {
//        ProductRequest productRequest = new ProductRequest();
//        productRequest.setProduct_Name("test food product");
//
//        String json = objectMapper.writeValueAsString(productRequest);
//
//        RequestBuilder requestBuilder = MockMvcRequestBuilders
//                .put("/products/{productId}", 3)
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(json);
//
//        mockMvc.perform(requestBuilder)
//                .andExpect(status().is(400));
//
//    }
//
//    @Transactional
//    @Test
//    public void updateProduct_productNotFound() throws Exception {
//        ProductRequest productRequest = new ProductRequest();
//        productRequest.setProduct_Name("test food product");
//        productRequest.setCategory(ProductCategory.FOOD);
//        productRequest.setImage_url("http://test.com");
//        productRequest.setPrice(100);
//        productRequest.setStock(2);
//
//        String json = objectMapper.writeValueAsString(productRequest);
//
//        RequestBuilder requestBuilder = MockMvcRequestBuilders
//                .put("/products/{productId}", 20000)
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(json);
//
//        mockMvc.perform(requestBuilder)
//                .andExpect(status().is(404));
//    }
//
//    // 刪除商品
//    @Transactional
//    @Test
//    public void deleteProduct_success() throws Exception {
//        RequestBuilder requestBuilder = MockMvcRequestBuilders
//                .delete("/products/{productId}", 5);
//
//        mockMvc.perform(requestBuilder)
//                .andExpect(status().is(204));
//    }
//
//    @Transactional
//    @Test
//    public void deleteProduct_deleteNonExistingProduct() throws Exception {
//        RequestBuilder requestBuilder = MockMvcRequestBuilders
//                .delete("/products/{productId}", 20000);
//
//        mockMvc.perform(requestBuilder)
//                .andExpect(status().is(204));
//    }
//
//    // 查詢商品列表
//    @Test
//    public void getProducts() throws Exception {
//        RequestBuilder requestBuilder = MockMvcRequestBuilders
//                .get("/products");
//
//        mockMvc.perform(requestBuilder)
//                .andDo(print())
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.limit", notNullValue()))
//                .andExpect(jsonPath("$.offset", notNullValue()))
//                .andExpect(jsonPath("$.total", notNullValue()))
//                .andExpect(jsonPath("$.results", hasSize(5)));
//    }
//
//    @Test
//    public void getProducts_filtering() throws Exception {
//        RequestBuilder requestBuilder = MockMvcRequestBuilders
//                .get("/products")
//                .param("search", "B")
//                .param("category", "CAR");
//
//        mockMvc.perform(requestBuilder)
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.limit", notNullValue()))
//                .andExpect(jsonPath("$.offset", notNullValue()))
//                .andExpect(jsonPath("$.total", notNullValue()))
//                .andExpect(jsonPath("$.results", hasSize(2)));
//    }
//
//    @Test
//    public void getProducts_sorting() throws Exception {
//        RequestBuilder requestBuilder = MockMvcRequestBuilders
//                .get("/products")
//                .param("orderBy", "price")
//                .param("sort", "desc");
//
//        mockMvc.perform(requestBuilder)
//                .andDo(print())
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.limit", notNullValue()))
//                .andExpect(jsonPath("$.offset", notNullValue()))
//                .andExpect(jsonPath("$.total", notNullValue()))
//                .andExpect(jsonPath("$.results", hasSize(5)))
//                .andExpect(jsonPath("$.results[0].product_Id", equalTo(6)))
//                .andExpect(jsonPath("$.results[1].product_Id", equalTo(5)))
//                .andExpect(jsonPath("$.results[2].product_Id", equalTo(7)))
//                .andExpect(jsonPath("$.results[3].product_Id", equalTo(4)))
//                .andExpect(jsonPath("$.results[4].product_Id", equalTo(2)));
//    }
//
//    @Test
//    public void getProducts_pagination() throws Exception {
//        RequestBuilder requestBuilder = MockMvcRequestBuilders
//                .get("/products")
//                .param("limit", "2")
//                .param("offset", "2");
//
//        mockMvc.perform(requestBuilder)
//                .andDo(print())
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.limit", notNullValue()))
//                .andExpect(jsonPath("$.offset", notNullValue()))
//                .andExpect(jsonPath("$.total", notNullValue()))
//                .andExpect(jsonPath("$.results", hasSize(2)))
//                .andExpect(jsonPath("$.results[0].product_Id", equalTo(5)))
//                .andExpect(jsonPath("$.results[1].product_Id", equalTo(4)));
//    }
//}
//
//
//
//
//
//
