package com.springbook.user.dao;

import com.springbook.user.domain.User;
import org.springframework.dao.EmptyResultDataAccessException;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDao {

    private DataSource dataSource;

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void add(User user) throws SQLException {
        Connection c = dataSource.getConnection();

        PreparedStatement ps = c.prepareStatement(
                "insert into users(id, name, password) values(?,?,?)");
        ps.setString(1, user.getId());
        ps.setString(2, user.getName());
        ps.setString(3, user.getPassword());

        ps.executeUpdate();

        ps.close();
        c.close();
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
        /* StatementStrategy st = new DeleteAllStatement(); // 선정한 전략 구현체 생성
        jdbcContextWithStatementStrategy(st); // 컨텍스트 호출하며 전략 구현체 전달 */
        jdbcContextWithStatementStrategy(new DeleteAllStatement()); // 인라인화
        // 클라이언트가 컨텍스트가 사용할 전략을 정해서 전달한다는 점에서 DI라 볼 수 있음 
        // 마이크로 DI - 클라이언트가 오브젝트 팩토리의 책임을 함께 지고 있음, DI의 장점을 단순화해서 IoC 컨테이너의 도움 없이 코드 내에서 적용
    }

    private void jdbcContextWithStatementStrategy(StatementStrategy stmt) throws SQLException {
        // 컨텍스트에 해당하는 부분이 메서드로 분리된 것 // 아직 클래스를 분리하진 않았고, 메서드로 분리되었다.
        // stmt == 클라이언트가 컨텍스트를 호출할 때 넘겨줄 전략 파라미터
        Connection c = null;
        PreparedStatement ps = null;

        try {
            c = dataSource.getConnection();

//            StatementStrategy strategy = new DeleteAllStatement(); // 컨텍스트가 사용할 전략을 클라이언트가 생성 후 주입한다.
//            ps = strategy.makePreparedStatement(c);
            ps = stmt.makePreparedStatement(c);

            ps.executeUpdate();
        } catch (SQLException e) {
            throw e;
        } finally {
            if (ps != null) { try { ps.close(); } catch (SQLException e) {} }
            if (c != null) { try { c.close(); } catch (SQLException e) {} }
        }
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
