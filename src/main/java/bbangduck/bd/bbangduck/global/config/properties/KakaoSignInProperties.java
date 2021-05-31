package bbangduck.bd.bbangduck.global.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 작성자 : 정구민 <br><br>
 *
 * 카카오 로그인 구현 시 필요한 Properties 를 application.yml 에서 관리하기 위해 필요한 ConfigurationProperties
 */
@Component
@Getter
@Setter
@ConfigurationProperties(prefix = "kakao.sign-in.properties")
public class KakaoSignInProperties {

    private String nativeAppKey;

    private String restApiKey;

    private String javaScriptKey;

    private String adminKey;

    private String redirectUri;

    private String clientSecret;

    private String authorizeState;

    private String scope;

}
