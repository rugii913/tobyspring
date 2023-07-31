/* 테이블 생성 SQL p.631*/
/*
 * KEY와 SQL 모두 일반적으로 DB에서 키워드로 사용되기 때문에 그대로 피륻 이름으로 쓸 수 없다.
 * 번거롭게 앞뒤에 "를 붙여서 사용하는 것을 피하기 위해 뒤에 _를 추가했다.
  */
CREATE TABLE SQLMAP (
  KEY_ VARCHAR(100) PRIMARY KEY,
  SQL_ VARCHAR(100) NOT NULL
);