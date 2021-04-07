package bbangduck.bd.bbangduck.hello;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class HelloTest {

    @PersistenceContext
    EntityManager em;

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

        System.out.println("findHello.toString() = " + findHello.toString());

    }

}