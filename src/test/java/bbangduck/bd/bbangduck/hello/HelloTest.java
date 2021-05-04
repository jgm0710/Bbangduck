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