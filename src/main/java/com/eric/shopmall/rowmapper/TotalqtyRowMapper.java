package com.eric.shopmall.rowmapper;

import com.eric.shopmall.model.Totalqty;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

public class TotalqtyRowMapper implements RowMapper<Totalqty> {

    @Override
    public Totalqty mapRow(ResultSet resultSet, int i) throws SQLException {

        Totalqty totalqty = new Totalqty();

        Timestamp purchaseTimestamp = resultSet.getTimestamp("purchase_date");
        totalqty.setCreate_date(purchaseTimestamp);
        totalqty.setQuantity(resultSet.getInt("daily_quantity_sum"));


        return totalqty;
    }
}
