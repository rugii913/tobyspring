package com.springbook.user.sqlservice;

public interface SqlRegistry {

    void registerSql(String key, String sql); // -> SQL을 키와 함께 등록한다.

    String findSql(String key) throws SqlNotFoundException; // -> 키로 SQL을 검색한다. 검색이 실패하면 예외를 던진다.
    // * SqlNotFoundException이 원래 있는 예외도 아니고, 책에서도 작성한 적이 없어서 일단 임시로 예외 클래스 만들어놨음
}
