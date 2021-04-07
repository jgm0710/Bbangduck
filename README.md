<h1> 빵덕 Server API </h1>

<h2> 1. 프로젝트 설명 </h2>

- 방탈출 카페 랭킹 사이트 구축에 필요한 API 를 제공.
- GPS 기능을 통한 본인 위치 주변의 방탈출 카페 추천하는 기능을 담고 있다.

<h2> 2. 프로젝트 실행 </h2>

- 해당 프로젝트는 gradle 빌드 툴을 사용.
- 'gradlew build' 명령어를 통해 패키징
- 생성된 jar 파일을 통해 프로젝트 구동

<h2> 3. 프로젝트 문서 </h2>

- 현재는 배포 전이기 때문에 프로젝트 구동 후 'http://localhost:8080/docs/index.html' 경로로 이동하여
문서 열람 가능

<h2> 4. 핵심 의존성 </h2>

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