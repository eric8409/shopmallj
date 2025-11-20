package com.eric.shopmall.dao;

import com.eric.shopmall.dto.OrderQueryParams;
import com.eric.shopmall.model.Order;
import com.eric.shopmall.model.OrderItem;
import com.eric.shopmall.model.Totalqty;

import java.util.List;

public interface OrderDao {

     Integer countOrder(OrderQueryParams orderQueryParams);

     List<Order> getOrders(OrderQueryParams orderQueryParams);

     Order getOrderById(Integer orderId);

     List<OrderItem> getOrderItemsById(Integer orderId);

     Integer createOrder(Integer userId, Integer totalAmount);

     void createOrderItems(Integer orderId, List<OrderItem> orderItemList);

     List<Totalqty> getTotalQuantity();
}
