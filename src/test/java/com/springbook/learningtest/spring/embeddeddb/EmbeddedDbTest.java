package com.springbook.learningtest.spring.embeddeddb;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType.HSQL;

public class EmbeddedDbTest {

    EmbeddedDatabase db;
    JdbcTemplate template;

    @BeforeEach
    public void setUp() {
        db = new EmbeddedDatabaseBuilder()
                // HSQL, DERBY, H2 세 가지 중 하나로, 초기화 SQL이 호환만 된다면 DB 종류는 언제든지 바꿀 수 있다.
                .setType(HSQL)
                // 테이블 생성과 초기 데이터를 넣기 위한 스크립트 지정 - SQL 스크립트는 복수 개 지정 가능
                .addScript("classpath:/com/springbook/learningtest/spring/embeddeddb/schema.sql")
                .addScript("classpath:/com/springbook/learningtest/spring/embeddeddb/data.sql")
                // 주어진 조건에 맞는 내장형 DB를 준비하고 초기화 스크립트를 모두 실행한 뒤에
                // 이에 접근할 수 있는 EmbeddedDatabase(extends DataSource)를 돌려준다.
                .build();

        template = new JdbcTemplate(db);
    }

    @AfterEach
    // 매 테스트를 진행한 뒤에 DB를 종료한다.
    // 내장형 메모리 DB는 따로 저장하지 않는 한 어플리케이션과 함께 매번 새롭게 DB가 만들어지고 제거되는 생명주기를 갖는다.
    public void tearDown() {
        db.shutdown();
    }

    @Test
    public void initData() { // -> 초기화 스크립트를 통해 등록된 데이터를 검증하는 테스트다.
        assertThat(template.queryForObject("select count(*) from sqlmap", Integer.class)).isEqualTo(2);

        List<Map<String, Object>> list = template.queryForList("select * from sqlmap order by key_");
        assertThat((String) list.get(0).get("key_")).isEqualTo("KEY1");
        assertThat((String) list.get(0).get("sql_")).isEqualTo("SQL1");
        assertThat((String) list.get(1).get("key_")).isEqualTo("KEY2");
        assertThat((String) list.get(1).get("sql_")).isEqualTo("SQL2");
    }

    @Test
    public void insert() { // 새로운 데이터를 추가하고 이를 확인해본다.
        template.update("insert into sqlmap(key_, sql_) values(?,?)", "KEY3", "SQL3");

        assertThat(template.queryForObject("select count(*) from sqlmap", Integer.class)).isEqualTo(3);
    }
}
