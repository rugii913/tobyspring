package com.springbook.user.sqlservice;

import com.springbook.user.dao.UserDao;
import com.springbook.user.sqlservice.jaxb.SqlType;
import com.springbook.user.sqlservice.jaxb.Sqlmap;
import org.springframework.oxm.Unmarshaller;

import javax.annotation.PostConstruct;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import java.io.IOException;

public class OxmSqlService implements SqlService {

    private final OxmSqlReader oxmSqlReader = new OxmSqlReader();
    // -> final이므로 변경 불가능하다. OxmSqlService와 OxmSqlReader는 강하게 결합돼서 하나의 빈으로 등록되고 한 번에 설정할 수 있다.
    private SqlRegistry sqlRegistry = new HashMapSqlRegistry();
    // -> oxmSqlReader와 달리 단지 디폴트 오브젝트로 만들어진 프로퍼티다. 따라서 필요에 따라 DI를 통해 교체 가능하다.

    public void setSqlRegistry(SqlRegistry sqlRegistry) {
        this.sqlRegistry = sqlRegistry;
    }

    // OxmSqlService의 공개된 프로퍼티를 통해 DI 받은 것을 그대로 멤버 클래스(OxmSqlReader)의 객체에 전달한다.
    // 아래 두 setter들은 단일 빈 설정구조를 위한 창구 역할을 할 뿐이다.
    // (빈 설정 때 unmarshaller, sqlmapFile 프로퍼티 받아서 그대로 내부 클래스에 전달만 한다는 뜻)
    public void setUnmarshaller(Unmarshaller unmarshaller) {
        this.oxmSqlReader.setUnmarshaller(unmarshaller);
    }

    public void setSqlmapFile(String sqlmapFile) {
        this.oxmSqlReader.setSqlmapFile(sqlmapFile);
    }

    // SqlService 인터페이스에 대한 구현 코드는 BaseSqlService와 같다. (loadSql() 및 getSql(~)을 뜻함)
    @PostConstruct
    public void loadSql() {
        this.oxmSqlReader.read(this.sqlRegistry);
    }

    @Override
    public String getSql(String key) throws SqlRetrievalFailureException {
        try {
            return this.sqlRegistry.findSql(key);
        } catch (SqlNotFoundException e) {
            throw new SqlRetrievalFailureException(e.getMessage(), e);
        }
    }

    private class OxmSqlReader implements SqlReader { // private 멤버 클래스로 정의 - 톱레벨 클래스인 OxmSqlService만이 사용할 수 있다.

        private Unmarshaller unmarshaller;
        private final static String DEFAULT_SQLMAP_FILE = "sqlmap.xml";
        private String sqlmapFile = DEFAULT_SQLMAP_FILE;

        public void setUnmarshaller(Unmarshaller unmarshaller) {
            this.unmarshaller = unmarshaller;
        }

        public void setSqlmapFile(String sqlmapFile) {
            this.sqlmapFile = sqlmapFile;
        }

        @Override
        public void read(SqlRegistry sqlRegistry) {
            try {
                Source source = new StreamSource(UserDao.class.getResourceAsStream(this.sqlmapFile));
                Sqlmap sqlmap = (Sqlmap) this.unmarshaller.unmarshal(source);
                // -> OxmSqlService를 통해 전달받은 OXM 인터페이스 구현 오브젝트를 가지고 언마샬링 작업 수행
                for (SqlType sql : sqlmap.getSql()) {
                    sqlRegistry.registerSql(sql.getKey(), sql.getValue());
                }

            } catch (IOException e) {
                throw new IllegalArgumentException(this.sqlmapFile + "을 가져올 수 없습니다.", e);
                // -> 언마샬 작업 중 IO 에러가 났다면 설정을 통해 제공받은 XML 파일 이름이나 정보가 잘못됐을 가능성이 제일 높다.
                // 이런 경우에 가장 적합한 런타임 예외 중 하나인 IllegalArgumentException으로 포장해서 던진다.
            }
        }
    }
}
