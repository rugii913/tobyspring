package com.springbook.user.service;

import com.springbook.user.domain.User;

public class UserServiceTx implements UserService {

    UserService userService; // UserService 구현체를 DI 받는다.

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    // DI 받은 UserService 구현체에 모든 기능을 위임한다.
    @Override
    public void add(User user) {
        userService.add(user);
    }

    @Override
    public void upgradeLevels() {
        userService.upgradeLevels();
    }
}
