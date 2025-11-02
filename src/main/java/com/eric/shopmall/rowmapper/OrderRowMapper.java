package com.eric.shopmall.rowmapper;

import com.eric.shopmall.model.Order;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class OrderRowMapper implements RowMapper<Order> {

    @Override
    public Order mapRow(ResultSet resultSet, int i) throws SQLException {

        Order order = new Order();

        order.setOrder_id(resultSet.getInt("order_id"));
        order.setUser_id(resultSet.getInt("user_id"));
        order.setTotalAmount(resultSet.getInt("total_amount"));
        order.setCreate_date(resultSet.getTimestamp("created_date"));
        order.setLast_modified_date(resultSet.getTimestamp("last_modified_date"));

        return order;
    }
}
