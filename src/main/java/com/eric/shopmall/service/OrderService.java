package com.eric.shopmall.service;

import com.eric.shopmall.dto.CreateOrderRequest;
import com.eric.shopmall.dto.OrderQueryParams;
import com.eric.shopmall.model.Order;
import com.eric.shopmall.model.Totalqty;

import java.util.List;

public interface OrderService {

    Integer countOrder(OrderQueryParams orderQueryParams);

    List<Order> getOrders(OrderQueryParams orderQueryParams);

    Order getOrderById(Integer orderId);

    Integer createOrder(Integer userId, CreateOrderRequest createOrderRequest);

    List<Totalqty> getTotalQuantity();

}
