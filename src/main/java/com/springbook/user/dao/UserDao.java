package com.springbook.user.dao;

import com.springbook.user.domain.User;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

public class UserDao {

    private DataSource dataSource; // 아직 get(), getCount()에서 사용하므로 남겨둬야함
    private JdbcTemplate jdbcTemplate;

    public void setDataSource(DataSource dataSource) {

        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.dataSource = dataSource; // 아직 JdbcContext를 적용하지 않은 메서드를 위해 남겨놓음
    }

    public void add(final User user) {

        /*
        this.jdbcContext.workWithStatementStrategy(
                (connection) -> {
                    PreparedStatement ps
                            = connection.prepareStatement("insert into users(id, name, password) values(?,?,?)");
                    ps.setString(1, user.getId());
                    ps.setString(2, user.getName());
                    ps.setString(3, user.getPassword());

                    return ps;
                }
        );
        */
        this.jdbcTemplate.update("insert into users(id, name, password) values (?,?,?)",
                user.getId(), user.getName(), user.getPassword());
    }

    public User get(String id) {
        /*
        Connection c = dataSource.getConnection();

        PreparedStatement ps = c.prepareStatement(
                "select * from users where id = ?");
        ps.setString(1, id);

        ResultSet rs = ps.executeQuery();

        User user = null;
        if (rs.next()) {
            user = new User();
            user.setId(rs.getString("id"));
            user.setName(rs.getString("name"));
            user.setPassword(rs.getString("password"));
        }

        rs.close();
        ps.close();
        c.close();

        if (user == null) {
            throw new EmptyResultDataAccessException(1);
        }

        return user;
        */
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
//        this.jdbcContext.executeSql("delete from users");
        this.jdbcTemplate.update("delete from users");
    }

    public Integer getCount() {
        /*
        Connection c = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            c = dataSource.getConnection();

            ps = c.prepareStatement("select count(*) from users");

            rs = ps.executeQuery();
            rs.next();
            return rs.getInt(1);
        } catch (SQLException e) {
            throw e;
        } finally {
            if (rs != null) {
                try { rs.close(); }
                catch (SQLException e) { }
            }
            if (ps != null) {
                try { ps.close(); }
                catch (SQLException e) { }
            }
            if (c != null) {
                try { c.close(); }
                catch (SQLException e) { }
            }
        }
        */
        return this.jdbcTemplate.queryForObject("select count(*) from users", Integer.class);
        // queryForInt deprecated https://youngminz.netlify.app/posts/toby-spring-boot-in-2021
    }
}
