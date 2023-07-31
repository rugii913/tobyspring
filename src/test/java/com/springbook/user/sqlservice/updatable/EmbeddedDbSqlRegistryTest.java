package com.springbook.user.sqlservice.updatable;

import com.springbook.user.sqlservice.SqlUpdateFailureException;
import com.springbook.user.sqlservice.UpdatableSqlRegistry;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.fail;
import static org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType.HSQL;

public class EmbeddedDbSqlRegistryTest extends AbstractUpdatableSqlRegistryTest {

    EmbeddedDatabase db;

    @Override
    protected UpdatableSqlRegistry createUpdatableSqlRegistry() {
        db = new EmbeddedDatabaseBuilder()
                .setType(HSQL)
                .addScript("classpath:com/springbook/user/sqlservice/updatable/sqlRegistrySchema.sql")
                .build();

        EmbeddedDbSqlRegistry embeddedDbSqlRegistry = new EmbeddedDbSqlRegistry();
        embeddedDbSqlRegistry.setDataSource(db);

        return embeddedDbSqlRegistry;
    }

    @AfterEach
    public void tearDown() {
        db.shutdown();
    }

    @Test
    public void transactionalUpdate() {
        // 초기 상태를 확인한다. 이미 슈퍼클래스의 다른 테스트 메서드에서 확인하긴 했지만
        // 트랜잭션 롤백 후의 결과와 비교하여, 이 테스트의 목적인 롤백 후의 상태는 처음과 동일하다는 것을 보여주기 위해 넣었다.
        checkFindResult("SQL1", "SQL2", "SQL3");

        Map<String, String> sqlmap = new HashMap<>();
        sqlmap.put("KEY1", "Modified1");
        sqlmap.put("KEY9999!@#$", "Modified9999");
        // -> 두 번째 SQL의 키를 존재하지 않는 것으로 지정한다.
        // 이 때문에 테스트는 실패할 것이고, 그 때 롤백이 일어나는지 확인한다.

        try {
            sqlRegistry.updateSql(sqlmap);
            fail();
            // -> 예외가 발생해서 catch 블록으로 넘어가지 않으면 뭔가 잘못된 것이다.
            // 그때는 테스트를 강제로 실패하게 만들고 기대와 다르게 동작한 원인을 찾도록 해야 한다.
        } catch (SqlUpdateFailureException e) {
            checkFindResult("SQL1", "SQL2", "SQL3");
            // -> 첫번째 SQL은 정상적으로 수정했지만 트랜잭션이 롤백되기 때문에 다시 변경 이전 상태로 돌아와야 한다.
            // 트랜잭션이 적용되지 않는다면 변경된 채로 남아서 테스트는 실패할 것이다.
        }
    }
}
