package com.springbook.user.sqlservice.updatable;

import com.springbook.user.sqlservice.SqlNotFoundException;
import com.springbook.user.sqlservice.SqlUpdateFailureException;
import com.springbook.user.sqlservice.UpdatableSqlRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public abstract class AbstractUpdatableSqlRegistryTest {
    // -> UpdatableSqlRegistry 인터페이스를 구현한 모든 클래스에 대한 테스트를 만들 때 사용할 수 있는 추상 테스트 클래스이다.

    UpdatableSqlRegistry sqlRegistry;

    @BeforeEach
    public void setUp() {
        sqlRegistry = createUpdatableSqlRegistry();
        // 각 테스트 메서드에서 사용할 초기 SQL 정보를 미리 등록해둔다.
        sqlRegistry.registerSql("KEY1", "SQL1");
        sqlRegistry.registerSql("KEY2", "SQL2");
        sqlRegistry.registerSql("KEY3", "SQL3");
    }

    // 테스트 픽스처를 생성하는 부분만 추상 메서드로 만들어 두고, 서브클래스에서 이를 구현하도록 만든다.
    abstract protected UpdatableSqlRegistry createUpdatableSqlRegistry();

    @Test
    public void find() {
        checkFindResult("SQL1", "SQL2", "SQL3");
    }

    // 서브클래스에 테스트를 추가한다면 필요할 수 있다.
    // 따라서 서브클래스에 접근 가능하도록 protected로 변경한다.
    protected void checkFindResult(String expected1, String expected2, String expected3) {
        assertThat(sqlRegistry.findSql("KEY1")).isEqualTo(expected1);
        assertThat(sqlRegistry.findSql("KEY2")).isEqualTo(expected2);
        assertThat(sqlRegistry.findSql("KEY3")).isEqualTo(expected3);
    }

    @Test
    // 주어진 키에 해당하는 SQL을 찾을 수 없을 때 예외가 발생하는지를 확인한다.
    // 예외상황에 대한 테스트는 빼먹기 쉽기 때문에 항상 의식적으로 넣으려고 노력해야 한다.
    public void unknownKey() {
        assertThatThrownBy(() -> sqlRegistry.findSql("SQL9999!@#$")).isExactlyInstanceOf(SqlNotFoundException.class);
    }

    @Test
    // SQL 하나를 변경하는 기능에 대한 테스트이다.
    // 검증할 때는 변경된 SQL 외의 나머지 SQL은 그대로인지도 확인해주는 게 좋다.
    public void updateSingle() {
        sqlRegistry.updateSql("KEY2", "Modified2");
        checkFindResult("SQL1", "Modified2", "SQL3");
    }

    @Test
    // 한 번에 여러 SQL을 수정하는 기능을 검증한다.
    public void updateMulti() {
        Map<String, String> sqlmap = new HashMap<>();
        sqlmap.put("KEY1", "Modified1");
        sqlmap.put("KEY3", "Modified3");

        sqlRegistry.updateSql(sqlmap);
        checkFindResult("Modified1", "SQL2", "Modified3");
    }

    @Test
    // 존재하지 않는 키의 SQL을 변경하려고 시도할 때 예외가 발생하는 것을 검증한다.
    public void updateWithNotExistingKey() {
        assertThatThrownBy(() -> sqlRegistry.updateSql("SQL9999!@#$", "Modified2")).isExactlyInstanceOf(SqlUpdateFailureException.class);
    }
}
