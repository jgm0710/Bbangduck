# 빵덕 Server API

# 1. 프로젝트 설명 

방탈출 카페 랭킹 사이트 구축에 필요한 API 를 제공.

# 2. 프로젝트 문서 

#### [API 문서 열람 링크](http://13.125.48.96:8080/docs/index.html)

# 3. 핵심 의존성 

- Spring boot
    - version : 2.4.4
    - Spring framework 설정을 보다 편하게 하기 위한 의존성

- Spring Data Jpa
    - ORM(Object Relational Mapping) 
        - 객체와 관계형 데이터베이스의 데이터를 자동으로 매핑해주는 기능    
        - Class 와 Table 간의 Paradigm 불일치 문제를 해결하기 위해 사용
    
- Spring Rest Docs
    - Rest API 문서화를 위해 사용
    - MockMvc 를 통해 테스트된 코드를 문서 조각(snippet)으로 생성
    - 생성된 문서 조각을 .adoc 파일에 끼워 넣어서 문서 생성
    
- Spring Security
    - Application 의 보안(인증, 권한, 인가 등)을 담당하는 스프링 하위 프레임워크
    
- Querydsl
    - Jpa 를 사용한 동적 쿼리를 보다 쉽게 사용하기 위한 의존성
