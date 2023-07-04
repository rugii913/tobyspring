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

    // 예외 전환 기능을 가진 DAO 메서드 - 의미를 명확하게 하기 위해 다른 예외로 전환
    public void exceptionTranslation() throws DuplicateUserIdException, SQLException {
        try {
            // ex. JDBC를 이용해 user 정보를 DB에 추가하는 코드 또는
            // 그런 기능을 가진 다른 SQL Exception을 던지는 메서드를 호출하는 코드 등
            this.jdbcContext.executeSql("delete from users");
        } catch (SQLException e) {
            // ErrorCode가 MySQL의 "Duplicate Entry(1062)"이면 예외 전환
            if (e.getErrorCode() == MysqlErrorNumbers.ER_DUP_ENTRY) {
                throw DuplicateUserIdException();
            } else {
                throw e; // 그 외의 경우는 SQLException 그대로
            }
        }
    }

    // 중첩 예외 - 주로 예외처리를 강제하는 체크 예외를 언체크 예외인 런타임 예외로 바꾸는 경우에 사용
    // 중첩 예외(nested exception) 예시 1: 새로운 예외를 만들면서 생성자에 근본 원인이 되는 예외를 넣어주기
    public void exceptionTranslation() throws DuplicateUserIdException, SQLException {
        try {
            // 예외 발생 가능성 있는 코드
        } catch (SQLException e) {
            if (e.getErrorCode() == MysqlErrorNumbers.ER_DUP_ENTRY) {
                throw DuplicateUserIdException(e); // -> 새로운 예외를 만들면서 생성자에 근본 원인이 되는 예외를 넣어주기
            } else {
                throw e;
            }
        }
    }

    // 중첩 예외(nested exception) 예시 2: 새로운 예외를 만들면서 initCause() 메서드에 근본 원인이 되는 예외를 넣어주기
    public void exceptionTranslation() throws DuplicateUserIdException, SQLException {
        try {
            // 예외 발생 가능성 있는 코드
        } catch (SQLException e) {
            if (e.getErrorCode() == MysqlErrorNumbers.ER_DUP_ENTRY) {
                throw DuplicateUserIdException().initCause(e); // -> 새로운 예외를 만들면서 initCause() 메서드에 근본 원인이 되는 예외를 넣어주기
            } else {
                throw e;
            }
        }
    }

    /*
        어차피 복구하지 못할 예외라면
        - 어플리케이션 코드에서는 런타임 예외로 포장해서 던져버리고,
        - 예외처리 서비스 등을 이용해 자세한 로그를 남기고,
        - 관리자에게는 메일 등으로 통보해주고,
        - 사용자에게는 친절한 안내 메시지를 보여주는 식으로
        처리하는 게 바람직하다.
    */
}
