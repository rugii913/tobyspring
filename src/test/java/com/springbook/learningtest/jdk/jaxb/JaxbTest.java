package com.springbook.learningtest.jdk.jaxb;


import com.springbook.user.sqlservice.jaxb.SqlType;
import com.springbook.user.sqlservice.jaxb.Sqlmap;
import org.junit.jupiter.api.Test;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class JaxbTest {

    @Test
    public void readSqlmap() throws JAXBException, IOException {
        String contextPath = Sqlmap.class.getPackage().getName();
        JAXBContext context = JAXBContext.newInstance(contextPath); // -> 바인딩용 클래스들의 위치를 갖고 JAXB 컨텍스트를 만든다.
        Unmarshaller unmarshaller = context.createUnmarshaller(); // -> 언마샬러 생성

        Sqlmap sqlmap = (Sqlmap) unmarshaller.unmarshal(getClass().getResourceAsStream("sqlmap.xml"));
        // -> 언마샬을 하면 매핑된 객체 트리의 루트인 Sqlmap을 돌려준다.
        // -> resources 폴더 기준으로 테스트 클래스와 같은 경로에 있는 sqlmap.xml을 사용

        List<SqlType> sqlList = sqlmap.getSql();

        // List에 담겨 있는 Sql 객체를 가져와 XML 문서와 같은 정보를 갖고 있는지 확인한다.
        assertThat(sqlList.size()).isEqualTo(3);
        assertThat(sqlList.get(0).getKey()).isEqualTo("add");
        assertThat(sqlList.get(0).getValue()).isEqualTo("insert");
        assertThat(sqlList.get(1).getKey()).isEqualTo("get");
        assertThat(sqlList.get(1).getValue()).isEqualTo("select");
        assertThat(sqlList.get(2).getKey()).isEqualTo("delete");
        assertThat(sqlList.get(2).getValue()).isEqualTo("delete");
    }
}
