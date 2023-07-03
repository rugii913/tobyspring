package com.springbook.user.dao;

import com.springbook.user.domain.User;
import org.springframework.dao.EmptyResultDataAccessException;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDao {

    private DataSource dataSource; // 아직 get(), getCount()에서 사용하므로 남겨둬야함
    private JdbcContext jdbcContext;

    public void setDataSource(DataSource dataSource) { // 수정자 메서드이면서 JdbcContext에 대한 생성, DI 작업을 동시에 수행
        this.jdbcContext = new JdbcContext(); // JdbcContext 인스턴스 생성(IoC)
        this.jdbcContext.setDataSource(dataSource); // DI

        this.dataSource = dataSource; // 아직 JdbcContext를 적용하지 않은 메서드를 위해 남겨놓음
    }

    public void add(final User user) throws SQLException {

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
    }

    public User get(String id) throws SQLException {
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
    }

    public void deleteAll() throws SQLException {
        executeSql("delete from users"); // 변하는 SQL 문장
    }

    private void executeSql(final String query) throws SQLException { // 바인딩할 파라미터 없이 SQL만 전달하면 되는 경우
        this.jdbcContext.workWithStatementStrategy( // 변하지 않는 부분: 어떤 query를 넣은 람다식을 인자로 전달하여 메서드 실행
                (connection) -> connection.prepareStatement(query)
        );
    }

    public int getCount() throws SQLException {
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
                try {
                    rs.close();
                } catch (SQLException e) {
                }
            }
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) {
                }
            }
            if (c != null) {
                try {
                    c.close();
                } catch (SQLException e) {
                }
            }
        }
    }
}
