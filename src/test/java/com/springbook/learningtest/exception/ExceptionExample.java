package com.springbook.learningtest.exception;

import com.springbook.user.dao.JdbcContext;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

import java.sql.SQLException;

/*
    예외를 처리할 때 반드시 지켜야 할 핵심 원칙은 한 가지다.
    모든 예외는 적절하게 복구되든지 아니면 작업을 중단시키고 운영자 또는 개발자에게 분명하게 통보돼야 한다.
*/
public class ExceptionExample {

    JdbcContext jdbcContext;

    public ExceptionExample() {
        this.jdbcContext.setDataSource(new SingleConnectionDataSource(
                "jdbc:mysql://localhost/testdb", "spring", "book", true));
    }

    // 예외 블랙홀
    public void exceptionBlackHole1() { // 예외 블랙홀 1
        try {
            this.jdbcContext.executeSql("delete from users");
        } catch (SQLException e) {
            // 예외를 catch 하고 아무것도 하지 않는다.
            // 예외 발생을 무시하고 정상적인 상황인 것처럼 다음 라인으로 넘어가겠다는 분명한 의도가 있는 게 아니라면
            // 연습 중에도 절대 만들어서는 안 되는 코드다.
        }
    }

    public void exceptionBlackHole2() { // 예외 블랙홀 2
        try {
            this.jdbcContext.executeSql("delete from users");
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    public void exceptionBlackHole3() { // 예외 블랙홀 3
        try {
            this.jdbcContext.executeSql("delete from users");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 그나마 나은 예외 처리
    public void exceptionBlackHole4() { // 굳이 예외를 잡아서 뭔가 조치를 취할 게 없다면 잡지 말라는 뜻이다. 차라리 메서드 밖으로 예외를 던져라
        try {
            this.jdbcContext.executeSql("delete from users");
        } catch (SQLException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    // 무의미하고 무책임한 throws
    public void irresponsibleThrows() throws Exception { // 구체적인 정보 없이 Exception으로 퉁쳐서 던지면 아무런 정보도 주지 않는다.
        this.jdbcContext.executeSql("delete from users");
    }
}
