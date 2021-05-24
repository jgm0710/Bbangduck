package bbangduck.bd.bbangduck.global.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.ssl.SSLContexts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.filter.ShallowEtagHeaderFilter;

import javax.net.ssl.SSLContext;
import javax.persistence.EntityManager;
import javax.servlet.Filter;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.Set;

/**
 * 작성자 : 정구민 <br><br>
 *
 * Application 전반에 걸쳐 의존성 주입을 통해 사용할 Class 들을 Bean 으로 등록하기 위해 구현한 Configuration Class
 */
@Configuration
public class AppConfig {

    @Bean
    JPAQueryFactory jpaQueryFactory(EntityManager entityManager) {
        return new JPAQueryFactory(entityManager);
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public RestTemplate restTemplate() throws KeyStoreException, NoSuchAlgorithmException, KeyManagementException {
        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
        requestFactory.setConnectTimeout(15000);
        requestFactory.setReadTimeout(15000);
        requestFactory.setBufferRequestBody(false);

        TrustStrategy acceptingTrustStrategy = (X509Certificate[] chain, String authType) -> true;

        SSLContext sslContext = SSLContexts.custom()
                .loadTrustMaterial(null, acceptingTrustStrategy)
                .build();

        SSLConnectionSocketFactory csf = new SSLConnectionSocketFactory(sslContext);

        requestFactory.setBufferRequestBody(false);
        CloseableHttpClient httpClient = HttpClientBuilder.create()
                .setMaxConnTotal(500)
                .setMaxConnPerRoute(100)
                .setSSLSocketFactory(csf)
                .build();
        requestFactory.setHttpClient(httpClient);

        return new RestTemplate(requestFactory);
    }

    @Bean
    public ApplicationRunner applicationRunner() {
        return new ApplicationRunner() {

            @Autowired
            private MemberService memberService;

            @Autowired
            private MemberRepository memberRepository;

            @Override
            public void run(ApplicationArguments args) {
                PasswordEncoder passwordEncoder = passwordEncoder();
                String encodedPassword = passwordEncoder.encode("bbangduckDEV7");
                String email = "developer@bbangduck.com";

                if (memberRepository.findByEmail(email).isEmpty()) {
                    Member member = Member.builder()
                            .email(email)
                            .password(encodedPassword)
                            .nickname("developer")
                            .description("개발자")
                            .roomEscapeRecordsOpenYN(false)
                            .refreshInfo(RefreshInfo.init(1000))
                            .roles(Set.of(MemberRole.DEVELOP, MemberRole.USER, MemberRole.ADMIN))
                            .build();

                    memberRepository.save(member);
                }

            }
        };
    }

}
