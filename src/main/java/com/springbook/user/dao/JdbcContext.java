package com.springbook.user.dao;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class JdbcContext {

    private DataSource dataSource;
    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    } // DataSource 타입 빈을 DI 받을 수 있도록 필드와 setter 준비

    public void workWithStatementStrategy(StatementStrategy stmt) throws SQLException {
        // 메서드 이름 변경 jdbcContextWithStatementStrategy -> workWithStatementStrategy
        // 메서드로 있던 것을 클래스로 분리, 이제 클래스 자체가 컨택스트
        Connection c = null;
        PreparedStatement ps = null;

        try {
            c = dataSource.getConnection();

            ps = stmt.makePreparedStatement(c); // 컨텍스트가 사용할 전략을 클라이언트가 생성 후 주입한다.

            ps.executeUpdate();
        } catch (SQLException e) {
            throw e;
        } finally {
            if (ps != null) { try { ps.close(); } catch (SQLException e) {} }
            if (c != null) { try { c.close(); } catch (SQLException e) {} }
        }
    }
}
