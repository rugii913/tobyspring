package com.springbook.user.service;

import com.springbook.user.domain.User;

public interface UserService {
    void add(User user);
    void upgradeLevels();
}
