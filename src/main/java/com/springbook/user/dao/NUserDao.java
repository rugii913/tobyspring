package com.springbook.user.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class NUserDao /*extends UserDao*/{
    /*@Override*/
    public Connection getConnection() throws ClassNotFoundException, SQLException {
        //~~~~~~~~~~~~~~~~~~~~~~~~
        //N사 DB connection 생성코드
        //~~~~~~~~~~~~~~~~~~~~~~~~
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection c = DriverManager.getConnection("jdbc:mysql://localhost/springbook", "spring", "book");
        return c;
    }
}
