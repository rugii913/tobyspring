package com.springbook.user.dao;

import com.springbook.user.domain.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import javax.sql.DataSource;
import java.util.List;

public class UserDao {

    private RowMapper<User> userMapper = (rs, rowNum) -> { // 재사용 가능하도록 RowMapper 독립
        User user = new User();
        user.setId(rs.getString("id"));
        user.setName(rs.getString("name"));
        user.setPassword(rs.getString("password"));
        return user;
    };
    private JdbcTemplate jdbcTemplate;

    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        // DataSource 객체는 JdbcTemplate을 만든 후에는 사용하지 않으므로 필드에 저장하지 않아도 된다.
        // 수정자 메서드에서 이렇게 다른 객체를 생성하는 경우도 종종 있다.
        // JdbcTemplate을 직접 스프링 빈으로 등록하는 방식을 사용하고 싶다면 setDataSource를 setJdbcTemplate으로 바꾸기만 하면 됨.
    }

    public void add(final User user) {
        this.jdbcTemplate.update("insert into users(id, name, password) values (?,?,?)",
                user.getId(), user.getName(), user.getPassword());
    }

    public User get(String id) {
        return this.jdbcTemplate.queryForObject("select * from users where id = ?", this.userMapper, id);
    }

    public void deleteAll() {
        this.jdbcTemplate.update("delete from users");
    }

    public Integer getCount() {
        return this.jdbcTemplate.queryForObject("select count(*) from users", Integer.class);
    }

    public List<User> getAll() {
        return this.jdbcTemplate.query("select * from users order by id", this.userMapper);
    }
}
