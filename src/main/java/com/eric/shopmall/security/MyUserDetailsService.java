package com.eric.shopmall.security;

import com.eric.shopmall.dao.UserDao;
import com.eric.shopmall.model.Role;
import com.eric.shopmall.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class MyUserDetailsService implements UserDetailsService {

    @Autowired
    private UserDao userDao;

    // 此方法保留原樣，專門用於使用者使用 email 登入時的認證
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // 從資料庫中查詢 User 數據
        User user =  userDao.getUserByEmail(email);

        if(user == null){
            throw new UsernameNotFoundException("user not found for: " + email);
        }else{
            String userPassword = user.getPassword();
            List<Role> roleList = userDao.getRolesByUserId(user.getUser_id());
            List<GrantedAuthority> authorities = convertAuthorities(roleList);

            // 注意：這裡返回的 username 是 email！
            return new org.springframework.security.core.userdetails.User(user.getEmail(), userPassword, authorities);
        }
    }

    /**
     * 新增一個使用 userId 查詢的方法，專供 JwtFilter 使用，提高可讀性。
     * @param userIdStr 字串形式的使用者 ID
     */
    public UserDetails loadUserByUserId(String userIdStr) throws UsernameNotFoundException {
        Integer userId = null;
        try {
            userId = Integer.valueOf(userIdStr);
        } catch (NumberFormatException e) {
            throw new UsernameNotFoundException("Invalid userId format: " + userIdStr);
        }

        // 使用 ID 查詢資料庫 (您需要確保 UserDao 有 getUserById 方法)
        User user = userDao.getUserById(userId);

        if (user == null) {
            throw new UsernameNotFoundException("user not found for id: " + userIdStr);
        } else {
            String userPassword = user.getPassword();
            List<Role> roleList = userDao.getRolesByUserId(user.getUser_id());
            List<GrantedAuthority> authorities = convertAuthorities(roleList);

            // 這裡返回的 UserDetails 的 getUsername() 欄位必須是 userId (字串化)
            return new org.springframework.security.core.userdetails.User(String.valueOf(user.getUser_id()), userPassword, authorities);
        }
    }

    private List<GrantedAuthority> convertAuthorities(List<Role> roleList){
        List<GrantedAuthority> authorities = new ArrayList<>();
        for(Role role : roleList){
            authorities.add(new SimpleGrantedAuthority(role.getRoleName()));
        }
        return authorities;
    }
}