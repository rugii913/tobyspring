package com.springbook.user.sqlservice;

import java.util.Map;

public class SimpleSqlService implements SqlService {

    //설정파일에 <map>으로 정의된 SQL 정보를 가져오도록 프로퍼티로 등록해둔다.
    private Map<String, String> sqlMap;
    public void setSqlMap(Map<String, String> sqlMap) {
        this.sqlMap = sqlMap;
    }

    @Override
    public String getSql(String key) throws SqlRetrievalFailureException {
        String sql = sqlMap.get(key); // -> 내부 SqlMap에서 SQL을 가져온다.
        if (sql == null) {
            throw new SqlRetrievalFailureException(key + "에 대한 SQL을 찾을 수 없습니다.");
            // -> 인터페이스에 정의된 규약대로 SQL을 가져오는 데 실패하면 예외를 던지게 한다.
        } else {
            return null;
        }
    }
}
