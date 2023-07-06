package com.springbook.user.service;

import com.springbook.user.domain.User;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

public class UserServiceTx implements UserService {

    UserService userService; // target 객체
    PlatformTransactionManager transactionManager;

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public void setTransactionManager(PlatformTransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    @Override // 메서드 구현과 위임
    public void add(User user) {
        userService.add(user);
    }

    @Override // 메서드 구현
    public void upgradeLevels() {
        TransactionStatus status = this.transactionManager.getTransaction(new DefaultTransactionDefinition()); // -> 부가기능 수행

        try {
            userService.upgradeLevels(); // -> 위임

            this.transactionManager.commit(status); // -> 부가기능 수행
        } catch (RuntimeException e) {
            this.transactionManager.rollback(status);
            throw e;
        }
    }
}
