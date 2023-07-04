package com.springbook.user.dao;

import com.springbook.user.domain.User;

import java.util.List;

public interface UserDao {
    void add(User user);
    User get(String id);
    List<User> getAll();
    void deleteAll();
    Integer getCount();


}
