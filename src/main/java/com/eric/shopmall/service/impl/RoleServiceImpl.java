package com.eric.shopmall.service.impl;

import com.eric.shopmall.dao.RoleDao;
import com.eric.shopmall.model.Role;
import com.eric.shopmall.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleDao roleDao;

    @Override
    public Role getRoleByName(String roleName) {

        return roleDao.getRoleByName(roleName);
    }


}
