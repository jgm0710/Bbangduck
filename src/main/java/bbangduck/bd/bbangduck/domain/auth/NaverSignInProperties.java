package bbangduck.bd.bbangduck.domain.auth;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.net.URI;

@Component
@ConfigurationProperties("naver.sign-in")
@Getter
@Setter
public class NaverSignInProperties {

    private String clientId;

    private String clientSecret;

    private String redirectUri;

    private String authorizeState;

    private String requestAccessTokenHostUriString;

    private String requestUserInfoHostUriString;

    public URI getRequestAccessTokenHostUri() {
        return URI.create(requestAccessTokenHostUriString);
    }

    public URI getRequestUserInfoHostUri() {
        return URI.create(requestUserInfoHostUriString);
    }

}
