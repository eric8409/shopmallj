package com.eric.shopmall.service.impl;

import com.eric.shopmall.dao.OrderDao;
import com.eric.shopmall.dao.ProductDao;
import com.eric.shopmall.dao.UserDao;
import com.eric.shopmall.dto.BuyItem;
import com.eric.shopmall.dto.CreateOrderRequest;
import com.eric.shopmall.dto.OrderQueryParams;
import com.eric.shopmall.model.*;
import com.eric.shopmall.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Component
public class OrderServiceImpl implements OrderService {

    private static final Logger log = LoggerFactory.getLogger(OrderServiceImpl.class);

    @Autowired
    private OrderDao orderDao;
    @Autowired
    private ProductDao productDao;
    @Autowired
    private UserDao userDao;

    @Override
    public Integer countOrder(OrderQueryParams orderQueryParams) {

        return orderDao.countOrder(orderQueryParams);
    }

    @Override
    public List<Order> getOrders(OrderQueryParams orderQueryParams) {

        List<Order> orderList = orderDao.getOrders(orderQueryParams);

        for(Order order: orderList) {
            List<OrderItem> OrderItemList = orderDao.getOrderItemsById(order.getOrder_id());

            order.setOrderItemList(OrderItemList);
        }
        return orderList;

    }

    @Override
    public Order getOrderById(Integer orderId) {


        Order order = orderDao.getOrderById(orderId);

        List<OrderItem> orderItemList  = orderDao.getOrderItemsById(orderId);

        order.setOrderItemList(orderItemList);

        return order;

    }

    @Transactional
    @Override
    public Integer createOrder(Integer userId, CreateOrderRequest createOrderRequest) {

        //檢查 User 是否存在
        User user = userDao.getUserById(userId);

        if(user == null) {

            log.warn("該 userId {} 不存在", userId);
            throw  new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }



        int totalAmount = 0;
        List<OrderItem> orderItemList = new ArrayList<>();

        for(BuyItem buyItem : createOrderRequest.getBuyItemList()) {
            Product product = productDao.getProductById(buyItem.getProductId());


            //檢查 product 是否存在、庫存是否足夠
            if(product == null) {

                log.warn("商品 {} 不存在", buyItem.getProductId());
                throw  new ResponseStatusException(HttpStatus.BAD_REQUEST);

            } else if (product.getStock() < buyItem.getQuantity()) {

                log.warn("商品 {} 庫存數量不足，無法購買。剩餘庫存 {} ，欲購買數量 {}", buyItem.getProductId(), product.getStock(), buyItem.getQuantity());
                throw  new ResponseStatusException(HttpStatus.BAD_REQUEST);
            }

            //扣除商品庫存
            productDao.updateStock(product.getProduct_Id(), product.getStock() - buyItem.getQuantity());




            //計算總價錢
            int amount = buyItem.getQuantity() * product.getPrice();
            totalAmount = totalAmount + amount;


            //BuyItem 轉換 OrderItem

            OrderItem orderItem = new OrderItem();
            orderItem.setProduct_id(buyItem.getProductId());
            orderItem.setQuantity(buyItem.getQuantity());
            orderItem.setAmount(amount);

            orderItemList.add(orderItem);

        }


        //創建訂單
        Integer  orderId = orderDao.createOrder(userId, totalAmount);

        orderDao.createOrderItems(orderId, orderItemList);

        return orderId;


    }

    @Override
    public List<Totalqty> getTotalQuantity() {

        return orderDao.getTotalQuantity();
    }




}
