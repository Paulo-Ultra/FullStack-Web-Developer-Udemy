package com.javacorner.admin.service;

import com.javacorner.admin.entity.User;

public interface UserService {

    User loaUserByEmail(String email);

    User createUser(String email, String password);

    void assingRoleToUser(String email, String roleName);
}
