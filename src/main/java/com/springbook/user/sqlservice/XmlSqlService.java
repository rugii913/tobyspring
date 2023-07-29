package com.springbook.user.sqlservice;

import com.springbook.user.dao.UserDao;
import com.springbook.user.sqlservice.jaxb.SqlType;
import com.springbook.user.sqlservice.jaxb.Sqlmap;

import javax.annotation.PostConstruct;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class XmlSqlService implements SqlService, SqlRegistry, SqlReader {

    // 의존 객체를 DI 받을 수 있도록 인터페이스 타입의 프로퍼티로 선언해둔다.
    private SqlReader sqlReader;
    private SqlRegistry sqlRegistry;
    // sqlMap은 SqlRegistry 구현의 일부가 된다. 따라서 외부에서 직접 접근할 수 없다.
    private Map<String, String> sqlMap = new HashMap<>();
    // sqlMapFile은 SqlReader 구현의 일부가 된다. 따라서 SqlReader 구현 메서드를 통하지 않고는 접근하면 안 된다.
    private String sqlmapFile;

    @PostConstruct
    public void loadSql() {
        this.sqlReader.read(this.sqlRegistry);
    }

    public void setSqlReader(SqlReader sqlReader) {
        this.sqlReader = sqlReader;
    }

    public void setSqlRegistry(SqlRegistry sqlRegistry) {
        this.sqlRegistry = sqlRegistry;
    }

    // sqlmapFile은 SqlReader 구현의 일부가 된다. 따라서 SqlReader 구현 메서드를 통하지 않고는 접근하면 안 된다.
    public void setSqlmapFile(String sqlmapFile) {
        this.sqlmapFile = sqlmapFile;
    }

    @Override
    public String getSql(String key) throws SqlRetrievalFailureException {
        try {
            return this.sqlRegistry.findSql(key);
        } catch (SqlNotFoundException e) {
            throw new SqlRetrievalFailureException(e.getMessage(), e);
        }
    }

    @Override
    public String findSql(String key) throws SqlNotFoundException {
        String sql = sqlMap.get(key);
        if (sql == null) {
            throw new SqlNotFoundException(key + "에 대한 SQL을 찾을 수 없습니다.");
        } else {
            return sql;
        }
    }

    @Override
    public void registerSql(String key, String sql) {
        // -> HashMap이라는 저장소를 사용하는 구체적인 구현 방법에서 독립될 수 있도록 인터페이스의 메서드로 접근하게 해준다.
        sqlMap.put(key, sql);
    }

    @Override
    public void read(SqlRegistry sqlRegistry) {
        // -> loadSql()에 있던 코드를 SqlReader 메서드로 가져온다. 초기화를 위해 무엇을 할 것인가와 SQL을 어떻게 읽는지를 분리한다.
        String contextPath = Sqlmap.class.getPackage().getName();
        try {
            JAXBContext context = JAXBContext.newInstance(contextPath);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            InputStream is = UserDao.class.getResourceAsStream(sqlmapFile);
            Sqlmap sqlmap = (Sqlmap) unmarshaller.unmarshal(is);
            for (SqlType sql : sqlmap.getSql()) {
                sqlRegistry.registerSql(sql.getKey(), sql.getValue());
                // -> SQL 저장 로직 구현에 독립적인 인터페이스 메서드를 통해 읽어들인 SQL과 키를 전달한다.
            }
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
    }
}
