package com.springbook.user.service;

import com.springbook.user.domain.User;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional // 아무런 속성 엘리먼트를 지정하지 않으면 디폴트 트랜잭션 속성 값 적용
public interface UserService {
    void add(User user);
    @Transactional(readOnly = true) // 대체 정책에 의해 우선 적용
    User get(String id);
    @Transactional(readOnly = true)
    List<User> getAll();
    void deleteAll();
    void update(User user);
    void upgradeLevels();
}
