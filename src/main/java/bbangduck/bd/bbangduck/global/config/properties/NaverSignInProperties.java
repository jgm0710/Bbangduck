package bbangduck.bd.bbangduck.global.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.net.URI;

/**
 * 소셜 로그인 API 중 네이버 API 로그인 구현을 위한
 * 키값, uri 등에 대한 정보, request uri 등등
 * 네이버 로그인을 구현하기 위해 필요한 값들 중
 * 설정 파일을 통해 관리하는 것이 편한 값들을 따로 빼기 위해 구현한
 * Properties
 *
 * @author jgm
 */
@Component
@ConfigurationProperties("social.sign-in.naver")
@Getter
@Setter
public class NaverSignInProperties {

    private String clientId;

    private String clientSecret;

    private String redirectUri;

    private String authorizeState;

    private String requestAccessTokenHostUriString;

    private String requestUserInfoHostUriString;

    public URI getRequestAccessTokenUri(String authorizationCode, String state) {
        return URI.create(requestAccessTokenHostUriString +
                "?grant_type=authorization_code" +
                "&client_id=" + clientId +
                "&client_secret=" + clientSecret +
                "&code=" + authorizationCode +
                "&state=" + state);
    }

    public URI getRequestUserInfoHostUri() {
        return URI.create(requestUserInfoHostUriString);
    }

    public MultiValueMap<String, String> getRequestUserInfoHeader(String accessToken) {
        LinkedMultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        return formData;
    }

}
