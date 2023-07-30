package com.springbook.learningtest.spring.oxm;

import com.springbook.user.sqlservice.jaxb.SqlType;
import com.springbook.user.sqlservice.jaxb.Sqlmap;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.oxm.Unmarshaller;
// -> JAXB API 등에서 사용하는 언마샬러와 클래스 이름이 같으므로 임포트할 때 주의
import org.springframework.oxm.XmlMappingException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration (locations = "/OxmTest-context.xml")
public class OxmTest {

    @Autowired
    Unmarshaller unmarshaller;
    // -> 스프링 테스트가 테스트용 어플리케이션 컨텍스트에서 Unmarshaller 인터페이스 타입 빈을 찾아서 테스트가 시작되기 전에 이 변수에 넣어준다.

    @Test
    public void unmarshallSqlMap() throws XmlMappingException, IOException {
        Source xmlSource = new StreamSource(getClass().getResourceAsStream("/com/springbook/learningtest/jdk/jaxb/sqlmap.xml"));
        // -> InputStream을 이용하는 Source 타입의 StreamSource를 만든다.

        Sqlmap sqlmap = (Sqlmap) this.unmarshaller.unmarshal(xmlSource);
        // -> 어떤 OXM 기술이든 언마샬은 이 한 줄이면 끝이다.

        List<SqlType> sqlList = sqlmap.getSql();
        assertThat(sqlList.size()).isEqualTo(3);
        assertThat(sqlList.get(0).getKey()).isEqualTo("add");
        assertThat(sqlList.get(2).getValue()).isEqualTo("delete");
    }
}
