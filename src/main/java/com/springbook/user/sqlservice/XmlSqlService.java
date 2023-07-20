package com.springbook.user.sqlservice;

import com.springbook.user.dao.UserDao;
import com.springbook.user.sqlservice.jaxb.SqlType;
import com.springbook.user.sqlservice.jaxb.Sqlmap;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class XmlSqlService implements SqlService {

    private Map<String, String> sqlMap = new HashMap<>(); // => 읽어온 SQL을 저장해둘 맵

    public XmlSqlService() { // 스프링이 객체를 만드는 시점에서 SQL을 읽어오도록 생성자를 이용한다.
        // JAXB API를 이용해 XML 문서를 오브젝트 트리로 읽어온다.
        String contextPath = Sqlmap.class.getPackage().getName();
        try {
            JAXBContext context = JAXBContext.newInstance(contextPath);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            InputStream is = UserDao.class.getResourceAsStream("sqlmap.xml"); // => UserDao와 같은 클래스패스의 sqlmap.xml 파일을 변환한다.
            Sqlmap sqlmap = (Sqlmap) unmarshaller.unmarshal(is);

            for (SqlType sql : sqlmap.getSql()) { // 읽어온 SQL을 맵으로 저장해둔다.
                sqlMap.put(sql.getKey(), sql.getValue());
            }
        } catch (JAXBException e) {
            throw new RuntimeException(e); // => JAXBException은 복구 불가능한 예외다. 불필요한 throws를 피하도록 런타임 예외로 포장해서 던진다.
        }
    }

    @Override
    public String getSql(String key) throws SqlRetrievalFailureException {
        String sql = sqlMap.get(key);

        if (sql == null) {
            throw new SqlRetrievalFailureException(key + "를 이용해서 SQL을 찾을 수 없습니다.");
        } else {
            return sql;
        }
    }
}
