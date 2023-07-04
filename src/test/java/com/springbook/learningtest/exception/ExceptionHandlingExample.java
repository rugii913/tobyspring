package com.springbook.learningtest.exception;

import com.springbook.user.dao.JdbcContext;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

import java.sql.SQLException;

public class ExceptionHandlingExample {

    JdbcContext jdbcContext;

    public ExceptionHandlingExample() {
        this.jdbcContext.setDataSource(new SingleConnectionDataSource(
                "jdbc:mysql://localhost/testdb", "spring", "book", true));
    }

    // 재시도를 통해 예외를 복구하는 코드
    public void exceptionRecover() throws Exception {
        class RetryFailedException extends Exception{
            // 직접 정의한 예외 RetryFailedException
        }

        final int MAX_RETRY = 3;

        int maxretry = MAX_RETRY;
        while(maxretry-- > 0) {
            try {
                // 예외가 발생할 가능성이 있는 시도
                this.jdbcContext.executeSql("delete from users");
                // 작업이 성공하면 finally로
            } catch (SQLException e) {
                // 로그 출력, 정해진 시간만큼 대기
            } finally {
                // 리소스 반납, 정리 작업
            }
        }
        throw new RetryFailedException(); // 최대 재시도 횟수를 넘기면 직접 예외 던지기
    }

    /*
        예외를 회피하는 것은 예외를 복구하는 것처럼 의도가 분명해야 한다.(p.288)
    */
    // 예외처리 회피 1
    public void exceptionRethrow1() throws SQLException {
        // ex. JDBC API
        this.jdbcContext.executeSql("delete from users");
    }

    // 예외처리 회피 2
    public void exceptionRethrow2() throws SQLException {
        try {
            // ex. JDBC API
            this.jdbcContext.executeSql("delete from users");
        } catch (SQLException e) {
            // 로그 출력 등... 작업 후 다시 던지기
            throw e;
        }
    }

}
