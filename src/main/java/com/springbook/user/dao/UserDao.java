package com.springbook.user.dao;

import com.springbook.user.domain.User;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.util.List;

public class UserDao {

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
        return this.jdbcTemplate.queryForObject("select * from users where id = ?",
                (rs, rowNum) -> { // ResultSet의 row 결과를 객체에 매핑해주는 RowMapper 콜백
                    User user = new User();
                    user.setId(rs.getString("id"));
                    user.setName(rs.getString("name"));
                    user.setPassword(rs.getString("password"));
                    return user;
                },
                id); // SQL에 바인딩할 파라미터 값

    /*
    !!!deprecated!!!
    public <T> T queryForObject(String sql, @Nullable Object[] args, RowMapper<T> rowMapper) throws DataAccessException {
        List<T> results = query(sql, args, new RowMapperResultSetExtractor<>(rowMapper, 1));
        return DataAccessUtils.nullableSingleResult(results);
    }

    ! 대신 이것 사용, args가 가변인자로 뒤로 들어감
    public <T> T queryForObject(String sql, RowMapper<T> rowMapper, @Nullable Object... args) throws DataAccessException {
        List<T> results = query(sql, args, new RowMapperResultSetExtractor<>(rowMapper, 1));
        return DataAccessUtils.nullableSingleResult(results);
    }
    */
    }

    public void deleteAll() {
        this.jdbcTemplate.update("delete from users");
    }

    public Integer getCount() {
        return this.jdbcTemplate.queryForObject("select count(*) from users", Integer.class);
        // queryForInt deprecated https://youngminz.netlify.app/posts/toby-spring-boot-in-2021
    }

    public List<User> getAll() {
        return this.jdbcTemplate.query("select * from users order by id",
                (rs, rowNum) -> {
                    User user = new User();
                    user.setId(rs.getString("id"));
                    user.setName(rs.getString("name"));
                    user.setPassword(rs.getString("password"));
                    return user;
                }
        );
    }
}
