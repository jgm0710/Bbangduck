package bbangduck.bd.bbangduck.hello;

import bbangduck.bd.bbangduck.domain.hello.Hello;
import bbangduck.bd.bbangduck.domain.hello.QHello;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static bbangduck.bd.bbangduck.domain.hello.QHello.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ActiveProfiles("test")
class HelloTest {

    @PersistenceContext
    EntityManager em;

    @Autowired
    JPAQueryFactory queryFactory;

    @Test
    @DisplayName("JPA 가 정상 동작하는지 테스트")
    @Transactional
    public void helloJpa() {
        //given
        Hello hello = Hello.builder()
                .name("hello")
                .build();

        //when
        em.persist(hello);

        Long helloId = hello.getId();

        em.flush();
        em.clear();

        //then
        Hello findHello = em.find(Hello.class, helloId);
        assertEquals(hello.getId(), findHello.getId());


    }

    @Test
    @DisplayName("accessToken slicing test")
    public void slicingTest() {
        //given
        String accessToken = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJybmFscm5hbDk5OUBuYXZlci5jb20iLCJyb2xlcyI6WyJST0xFX1VTRVIiXSwiaWF0IjoxNjIwMTQ2MTQ5LCJleHAiOjE2MjAxNzYxNDl9.tuRVCyu8zJOZfF58Hvyi_zckBvQaXAkXW18Gt6x9JHI";
        //when

        //then
        int i = accessToken.indexOf('.');
        System.out.println("i = " + i);
        String header = accessToken.substring(0,i);
        accessToken = accessToken.substring(i + 1);
        int i1 = accessToken.indexOf('.');
        String payload = accessToken.substring(0, i1);
        String signature = accessToken.substring(i1 + 1);
        System.out.println("header = " + header);
        System.out.println("payload = " + payload);
        System.out.println("signature = " + signature);


    }

    @Test
    @DisplayName("Querydsl 세팅 검증")
    @Transactional
    public void helloQueryDsl() {
        //given
        Hello hello = Hello.builder()
                .name("hello Querydsl")
                .build();

        em.persist(hello);

        em.flush();
        em.clear();

        //when
        Hello findHello = queryFactory.selectFrom(QHello.hello)
                .fetchOne();

        //then
        assert findHello != null;
        assertEquals(hello.getId(), findHello.getId());
    }

}