package com.eric.shopmall.dao.impl;

import com.eric.shopmall.dao.UserDao;
import com.eric.shopmall.dto.UserRegisterRequest;
import com.eric.shopmall.model.Product;
import com.eric.shopmall.model.Role;
import com.eric.shopmall.model.User;
import com.eric.shopmall.rowmapper.ProductRowMapper;
import com.eric.shopmall.rowmapper.RoleRowMapper;
import com.eric.shopmall.rowmapper.UserRowMapper;
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
public class UserDaoImpl implements UserDao {

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    @Autowired
    private RoleRowMapper roleRowMapper;

    @Override
    public User getUserById(Integer userId) {

        String sql = "SELECT user_id, email, password, created_date, last_modified_date FROM user WHERE user_id = :userId";


        Map<String, Object> map = new HashMap<>();
        map.put("userId", userId);

        List<User> userList = namedParameterJdbcTemplate.query(sql, map, new UserRowMapper());

        if (userList.size() > 0)
            return userList.get(0);
        else
            return null;

   }

    @Override
    public User getUserByEmail(String email) {

        String sql = "SELECT user_id, email, password, created_date, last_modified_date FROM user WHERE email = :email";


        Map<String, Object> map = new HashMap<>();
        map.put("email", email);

        List<User> userList = namedParameterJdbcTemplate.query(sql, map, new UserRowMapper());

        if (userList.size() > 0)
            return userList.get(0);
        else
            return null;

    }

    @Override
    public Integer createUser(UserRegisterRequest userRegisterRequest) {

        String sql = "INSERT INTO user (email, password, created_date, last_modified_date) " +
                "VALUES ( :email, :password, :created_date, :last_modified_date)";


        Map<String, Object> map = new HashMap<>();
        map.put("email", userRegisterRequest.getEmail());
        map.put("password", userRegisterRequest.getPassword());

        Date now = new Date();
        map.put("created_date", now);
        map.put("last_modified_date", now);

        KeyHolder keyHolder = new GeneratedKeyHolder();

        namedParameterJdbcTemplate.update(sql, new MapSqlParameterSource(map), keyHolder);

        int userId = keyHolder.getKey().intValue();
        return userId;
    }


    @Override
    public List<Role> getRolesByUserId(Integer userId) {
        String sql = "SELECT role.role_id, role.role_name " +
                     "FROM  role JOIN user_has_role " +
                     " ON role.role_id = user_has_role.role_id " +
                     "WHERE user_has_role.user_id = :userId";

        Map<String, Object> map = new HashMap<>();
        map.put("userId", userId);

        List<Role> roleList = namedParameterJdbcTemplate.query(sql, map, roleRowMapper);

        return roleList;
    }

    @Override
    public void addRoleForUserId(Integer userId, Role role) {

        String sql = "INSERT INTO user_has_role(user_id, role_id) VALUES (:userId, :roleId)";

        Map<String, Object> map = new HashMap<>();
        map.put("userId", userId);
        map.put("roleId", role.getRoleId());

        namedParameterJdbcTemplate.update(sql, map);
    }

    // *** 新增: 實現獲取所有使用者列表的方法 ***
    @Override
    public List<User> getAllUsers() {
        // 查詢所有使用者，但**不**包含敏感的 password 欄位
        String sql = "SELECT user_id, email, null as password, created_date, last_modified_date FROM user";

        // 注意：這裡依賴 UserRowMapper 能正確處理 password 為 null 的情況（例如，在 setter 中檢查 null）
        List<User> userList = namedParameterJdbcTemplate.query(sql, new UserRowMapper());

        return userList;
    }


}

