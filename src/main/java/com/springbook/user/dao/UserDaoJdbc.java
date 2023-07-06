package com.springbook.user.dao;

import com.springbook.user.domain.Level;
import com.springbook.user.domain.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import javax.sql.DataSource;
import java.util.List;

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

    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public void add(final User user) {
        this.jdbcTemplate.update(
                "insert into users(id, name, password, level, login, recommend, email) values (?,?,?,?,?,?,?)",
                user.getId(), user.getName(), user.getPassword(), user.getLevel().intValue(), user.getLogin(), user.getRecommend(), user.getEmail());
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

    @Override
    public int update(User user) {
        return this.jdbcTemplate.update(
                "update users set name = ?, password = ?, level = ?, login = ?, recommend = ?, email = ? where id = ?",
                user.getName(), user.getPassword(), user.getLevel().intValue(), user.getLogin(), user.getRecommend(), user.getEmail(), user.getId()
        );
    }
}
