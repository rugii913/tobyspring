package com.springbook.learningtest.exception;

import com.springbook.user.dao.JdbcContext;
import com.springbook.user.domain.User;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

import java.math.BigDecimal;
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
                throw new DuplicateUserIdException();
            } else {
                throw e; // 그 외의 경우는 SQLException 그대로
            }
        }
    }

    // 중첩 예외 - 주로 예외처리를 강제하는 체크 예외를 언체크 예외인 런타임 예외로 바꾸는 경우에 사용
    // 중첩 예외(nested exception) 예시 1: 새로운 예외를 만들면서 생성자에 근본 원인이 되는 예외를 넣어주기
    public void exceptionTranslation1() throws DuplicateUserIdException, SQLException {
        try {
            // 예외 발생 가능성 있는 코드
            this.jdbcContext.executeSql("delete from users");
        } catch (SQLException e) {
            if (e.getErrorCode() == MysqlErrorNumbers.ER_DUP_ENTRY) {
                throw new DuplicateUserIdException(e); // -> 새로운 예외를 만들면서 생성자에 근본 원인이 되는 예외를 넣어주기
            } else {
                throw e;
            }
        }
    }

    // 중첩 예외(nested exception) 예시 2: 새로운 예외를 만들면서 initCause() 메서드에 근본 원인이 되는 예외를 넣어주기
    public void exceptionTranslation2() throws DuplicateUserIdException, SQLException {
        try {
            // 예외 발생 가능성 있는 코드
            this.jdbcContext.executeSql("delete from users");
        } catch (SQLException e) {
            if (e.getErrorCode() == MysqlErrorNumbers.ER_DUP_ENTRY) {
                throw (DuplicateUserIdException) new DuplicateUserIdException().initCause(e); // -> 새로운 예외를 만들면서 initCause() 메서드에 근본 원인이 되는 예외를 넣어주기
            } else {
                throw e;
            }
        }
    }

    // p.293 리스트 4-13~14 예외처리 전략 - 런타임 예외의 보편화 적용 / 낙관적인 예외처리 기법
    public void add() throws DuplicateUserIdException { // DuplicateUserIdException은 런타임 예외이지만, 필요한 경우 활용할 수 있도록 선언에 포함시킨다.
        try {
            // ex. JDBC를 이용해 user 정보를 DB에 추가하는 코드 또는
            // 그런 기능을 가진 다른 SQL Exception을 던지는 메서드를 호출하는 코드 등
            this.jdbcContext.executeSql("delete from users");
        } catch (SQLException e) {
            if (e.getErrorCode() == MysqlErrorNumbers.ER_DUP_ENTRY) {
                throw new DuplicateUserIdException(e); //예외 전환
            } else {
                throw new RuntimeException(e); // 예외 포장
            }
        }
    }

    class DuplicateUserIdException extends RuntimeException {

        public DuplicateUserIdException() {
        }

        public DuplicateUserIdException(Throwable cause) {
            super(cause);
        }
    }

    class MysqlErrorNumbers {
        static final int ER_DUP_ENTRY = 1062;
    }

    /*
        어차피 복구하지 못할 예외라면
        - 어플리케이션 코드에서는 런타임 예외로 포장해서 던져버리고,
        - 예외처리 서비스 등을 이용해 자세한 로그를 남기고,
        - 관리자에게는 메일 등으로 통보해주고,
        - 사용자에게는 친절한 안내 메시지를 보여주는 식으로
        처리하는 게 바람직하다.
    */

    // p.296 리스트 4-15 예외처리 전략 - 어플리케이션 예외 / 비관적인 접근 방법
    public void applicationException() {
        Account account = new Account();
        BigDecimal amount = BigDecimal.valueOf(1_000_000_000);

        try {
            BigDecimal balance = account.withdraw(amount);
            // ...
            // 정상적인 처리 결과를 출력하도록 진행
        } catch (InsufficientBalanceException e) { // withdraw(~) 메서드의 체크 예외
            // InsufficientBalanceException에 담긴 인출 가능한 잔고금액 정보를 가져옴
            BigDecimal availFunds = e.getAvailFunds();
        }
    }

    class Account {
        public BigDecimal withdraw(BigDecimal amount) throws InsufficientBalanceException {
            if (amount != BigDecimal.valueOf(1_500_000_000)) throw new InsufficientBalanceException();
            return amount;
        }
    }

    private class InsufficientBalanceException extends Exception {
        public BigDecimal getAvailFunds() {
            return BigDecimal.valueOf(1_000_000_000);
        }
    }

    /*
        - 스프링의 JdbcTemplate은 런타임 예외 전략을 따르고 있다.
         -> 가능한 빨리 언체크/런타임 예외로 전환
         -> 앞서 JdbcContext에서 JdbcTemplate으로 바꿀 때 throws SQLException이 사라졌던 이유

        - 스프링의 다른 API 메서드에 정의되어 있는 예외 역시 런타임 예외
         -> 발생 가능한 예외가 있다고 하더라도 이를 처리하도록 강제하지 않음
    */

    // p.303 리스트 4-17~18 JDBC Template은 DB 에러 코드 매핑을 통해 예외 전환을 한다.
    public void addExceptionTranslation() throws DuplicateUserIdException { // 예외 전환해서 어플리케이션 레벨의 체크 예외 던지기
        try {
            // jdbcTemplate을 이용해 User를 add 하는 코드
            User user = new User("abc", "name", "password");
            JdbcTemplate jdbcTemplate = new JdbcTemplate(
                    new SingleConnectionDataSource("jdbc:mysql://localhost/testdb", "spring", "book", true)
            );
            jdbcTemplate.update("insert into users(id, name, password) values (?,?,?)",
                    user.getId(), user.getName(), user.getPassword());
        } catch (DuplicateKeyException e) {
            // 로그를 남기는 등의 필요한 작업
            throw new DuplicateUserIdException(e); // 예외를 전환할 때는 원인이 되는 예외를 중첩하는 것이 좋다.
        }
    }
}
