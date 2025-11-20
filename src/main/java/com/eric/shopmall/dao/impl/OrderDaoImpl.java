package com.eric.shopmall.dao.impl;

import com.eric.shopmall.dao.OrderDao;
import com.eric.shopmall.dto.OrderQueryParams;
import com.eric.shopmall.model.Order;
import com.eric.shopmall.model.OrderItem;
import com.eric.shopmall.model.Product;
import com.eric.shopmall.model.Totalqty;
import com.eric.shopmall.rowmapper.OrderItemRowMapper;
import com.eric.shopmall.rowmapper.OrderRowMapper;
import com.eric.shopmall.rowmapper.ProductRowMapper;
import com.eric.shopmall.rowmapper.TotalqtyRowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class OrderDaoImpl implements OrderDao {

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Override
    public Integer countOrder(OrderQueryParams orderQueryParams) {

        String sql = "select count(*) from `order` WHERE 1=1 ";

        Map<String, Object> map = new HashMap<>();

        //查詢條件
        sql = addFilteringSql(sql, map, orderQueryParams);

        Integer total = namedParameterJdbcTemplate.queryForObject(sql, map, Integer.class);

        return total;
    }

    @Override
    public List<Order> getOrders(OrderQueryParams orderQueryParams) {

        String sql =  "SELECT order_id, user_id, total_amount, created_date, last_modified_date " +
                "FROM `order` WHERE 1=1";

        Map<String, Object> map = new HashMap<>();

        //查詢條件
        sql = addFilteringSql(sql, map, orderQueryParams);


        //排序
        sql = sql + " ORDER BY  created_date DESC";

        //分頁
        sql = sql + " LIMIT :limit OFFSET :offset";
        map.put("limit", orderQueryParams.getLimit());
        map.put("offset", orderQueryParams.getOffset());

        List<Order> orderList = namedParameterJdbcTemplate.query(sql, map, new OrderRowMapper());

        return orderList;
    }


    @Override
    public Order getOrderById(Integer orderId) {

        String sql =  "SELECT order_id, user_id, total_amount, created_date, last_modified_date " +
                "FROM `order` WHERE order_id = :orderId";

        Map<String, Object> map = new HashMap<>();
        map.put("orderId", orderId);


        List<Order> orderList = namedParameterJdbcTemplate.query(sql, map, new OrderRowMapper());

        if(orderList.size()>0)
            return orderList.get(0);
        else
            return null;
    }

    @Override
    public List<OrderItem> getOrderItemsById(Integer orderId) {

        String sql = "SELECT oi.order_item_id, oi.order_id, oi.product_id, oi.quantity, oi.amount, p.product_name, p.image_url " +
                "FROM order_item as oi " +
                "LEFT JOIN  product as p " +
                "ON oi.product_id = p.product_id " +
                "WHERE order_item_id = :orderId";

        Map<String, Object> map = new HashMap<>();
        map.put("orderId", orderId);


        List<OrderItem> orderItemList = namedParameterJdbcTemplate.query(sql, map, new OrderItemRowMapper());

        return orderItemList;
    }


    @Override
    public Integer createOrder(Integer userId, Integer totalAmount) {

        String sql = "INSERT INTO `order` (user_id, total_amount, created_date, last_modified_date) " +
                "VALUES ( :userId, :totalAmount, :created_date, :last_modified_date)";


        Map<String, Object> map = new HashMap<>();
        map.put("userId", userId);
        map.put("totalAmount", totalAmount);

        Date now = new Date();
        map.put("created_date", now);
        map.put("last_modified_date", now);

        KeyHolder keyHolder = new GeneratedKeyHolder();

        namedParameterJdbcTemplate.update(sql, new MapSqlParameterSource(map), keyHolder);

        int orderId = keyHolder.getKey().intValue();
        return orderId;
    }

    @Override
    public void createOrderItems(Integer orderId, List<OrderItem> orderItemList) {


        for (OrderItem orderItem : orderItemList) {

            String sql = "INSERT INTO order_item (order_id, product_id, quantity, amount) " +
                    "VALUES ( :orderId, :productId, :quantity, :amount)";


            Map<String, Object> map = new HashMap<>();
            map.put("orderId", orderId);
            map.put("productId", orderItem.getProduct_id());
            map.put("quantity", orderItem.getQuantity());
            map.put("amount", orderItem.getAmount());

            namedParameterJdbcTemplate.update(sql, map);
        }

    }
    private  String addFilteringSql(String sql, Map<String, Object> map, OrderQueryParams orderQueryParams){

        //查詢條件
        if( orderQueryParams.getUserId() != null) {

            sql = sql + " AND user_id = :userId";
            map.put("userId", orderQueryParams.getUserId());
        }

        return  sql;
    }


    @Override
    public List<Totalqty> getTotalQuantity() {

        String sql = " SELECT DATE(o.created_date) AS purchase_date, " +
                " SUM(oi.quantity) AS daily_quantity_sum " +
                " FROM `order` AS o " +
                " JOIN `order_item` AS oi ON o.order_id = oi.order_id " +
                " GROUP BY DATE(o.created_date) " +
                " ORDER BY purchase_date ASC ";

        Map<String, Object> map = new HashMap<>();


        List<Totalqty> totalqtyList = namedParameterJdbcTemplate.query(sql, map, new TotalqtyRowMapper());


        return totalqtyList;
    }













}
