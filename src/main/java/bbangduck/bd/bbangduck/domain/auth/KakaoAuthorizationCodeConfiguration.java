package bbangduck.bd.bbangduck.domain.auth;

import bbangduck.bd.bbangduck.global.config.properties.KakaoLoginProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.net.URI;

@Component
@RequiredArgsConstructor
@Slf4j
public class KakaoAuthorizationCodeConfiguration {

    private final KakaoLoginProperties kakaoLoginProperties;

    private URI kakaoGetTokenUri = URI.create("https://kauth.kakao.com/oauth/token");

    private URI kakaoGetUserInfoUri = URI.create("https://kapi.kakao.com/v2/user/me");

    public URI getKakaoGetTokenUri() {
        return this.kakaoGetTokenUri;
    }

    public URI getKakaoGetUserInfoUri() {
        return this.kakaoGetUserInfoUri;
    }

    public MultiValueMap<String, String> getAccessTokenReqeustBody(String authorizationCode) {
        LinkedMultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("grant_type", "authorization_code");
        formData.add("client_id", kakaoLoginProperties.getRestApiKey());
        formData.add("redirect_uri", kakaoLoginProperties.getRedirectUri());
        formData.add("code", authorizationCode);
        formData.add("client_secret", kakaoLoginProperties.getClientSecret());

        return formData;
    }


    public MultiValueMap<String, String> getAccessTokenRequestHeader() {
        LinkedMultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE);
        return formData;
    }

    public MultiValueMap<String, String> getUserInfoRequestHeaher(String accessToken) {
        LinkedMultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
        formData.add("Authorization", "Bearer " + accessToken);
        return formData;
    }
}
