package com.springbook.user.dao;

import com.springbook.user.domain.Level;
import com.springbook.user.domain.User;
import com.springbook.user.sqlservice.SqlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;

@Component
public class UserDaoJdbc implements UserDao {

    private RowMapper<User> userMapper = (rs, rowNum) -> {
        User user = new User();
        user.setId(rs.getString("id"));
        user.setName(rs.getString("name"));
        user.setPassword(rs.getString("password"));
        user.setLevel(Level.valueOf(rs.getInt("level")));
        user.setLogin(rs.getInt("login"));
        user.setRecommend(rs.getInt("recommend"));
        user.setEmail(rs.getString("email"));
        return user;
    };
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private SqlService sqlService;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public void add(final User user) {
        this.jdbcTemplate.update(
                this.sqlService.getSql("userAdd"), // -> 프로퍼티로 제공받은 맵으로부터 키를 이용해서 필요한 SQL을 가져온다.
                user.getId(), user.getName(), user.getPassword(), user.getEmail(),
                user.getLevel().intValue(), user.getLogin(), user.getRecommend());
    }

    public User get(String id) {
        return this.jdbcTemplate.queryForObject(sqlService.getSql("userGet"), this.userMapper, id);
    }

    public void deleteAll() {
        this.jdbcTemplate.update(sqlService.getSql("userDeleteAll"));
    }

    public Integer getCount() {
        return this.jdbcTemplate.queryForObject(sqlService.getSql("userGetCount"), Integer.class);
    }

    public List<User> getAll() {
        return this.jdbcTemplate.query(sqlService.getSql("userGetAll"), this.userMapper);
    }

    @Override
    public void update(User user) {
        this.jdbcTemplate.update(
                sqlService.getSql("userUpdate"),
                user.getName(), user.getPassword(), user.getEmail(), user.getLevel().intValue(), user.getLogin(), user.getRecommend(), user.getId()
        );
    }
}
