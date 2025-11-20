package com.eric.shopmall.controller;

import com.eric.shopmall.dto.CreateOrderRequest;
import com.eric.shopmall.dto.OrderQueryParams;
import com.eric.shopmall.model.Order;
import com.eric.shopmall.model.Product;
import com.eric.shopmall.model.Totalqty;
import com.eric.shopmall.service.OrderService;
import com.eric.shopmall.util.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.List;

@RestController
public class OrderController {

    @Autowired
    private OrderService orderService;


    @CrossOrigin(origins = {"https://eric8409.github.io"})
//    @CrossOrigin(origins = {"https://eric8409.github.io", "http://localhost:4200"})
    @GetMapping("/users/{userId}/orders")
    public ResponseEntity<Page<Order>> getOrders(

            @PathVariable Integer userId,
            @RequestParam(defaultValue = "10") @Max(1000) @Min(0) Integer limit,
            @RequestParam(defaultValue = "0") @Min(0) Integer offset

    ) {

        OrderQueryParams orderQueryParams = new OrderQueryParams();
        orderQueryParams.setUserId(userId);
        orderQueryParams.setLimit(limit);
        orderQueryParams.setOffset(offset);

        //取得 order list
        List<Order> orderList = orderService.getOrders(orderQueryParams);

        //取得 count 總數
        Integer count = orderService.countOrder(orderQueryParams);

        //分頁
        Page<Order> page = new Page<>();
        page.setLimit(limit);
        page.setOffset(offset);
        page.setTotal(count);
        page.setResults(orderList);


        return  ResponseEntity.status(HttpStatus.OK).body(page);

    }

    @CrossOrigin(origins = {"https://eric8409.github.io"})
//    @CrossOrigin(origins = {"https://eric8409.github.io", "http://localhost:4200"})
    @PostMapping("/users/{userId}/orders")
    public ResponseEntity<?> createOrder(@PathVariable Integer userId,
                                         @RequestBody @Valid CreateOrderRequest createOrderRequest) {


        Integer orderId = orderService.createOrder(userId, createOrderRequest);

        Order order = orderService.getOrderById(orderId);

        return ResponseEntity.status(HttpStatus.CREATED).body(order);
    }

       @CrossOrigin(origins = {"https://eric8409.github.io"})
//    @CrossOrigin(origins = {"https://eric8409.github.io", "http://localhost:4200"})
    @GetMapping("/orders")
    public ResponseEntity<List<Totalqty>> getTotalQuantity() {

        List<Totalqty> totalqtyList  =  orderService.getTotalQuantity();

        if(totalqtyList != null)
            return ResponseEntity.status(HttpStatus.OK).body(totalqtyList);
        else
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

    }








}
















