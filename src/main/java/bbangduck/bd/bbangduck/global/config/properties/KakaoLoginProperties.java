package bbangduck.bd.bbangduck.global.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
@ConfigurationProperties(prefix = "kakao.login.properties")
public class KakaoLoginProperties {

    private String nativeAppKey;

    private String restApiKey;

    private String javaScriptKey;

    private String adminKey;

    private String redirectUri;

    private String clientSecret;

    private String authorizeState;

    private String scope;

}
