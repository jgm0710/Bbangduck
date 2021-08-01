package bbangduck.bd.bbangduck.domain.auth;

import bbangduck.bd.bbangduck.global.config.properties.KakaoSignInProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.net.URI;

/**
 * 작성자 : 정구민 <br><br>
 *
 * 카카오 로그인 요청 시 인증 토큰 조회 요청, 카카오 회원 정보 요청 시 필요한 Body, Header 에 대한 정보를 반환하기 위한 설정
 */
// TODO: 2021-06-21 properties 로 옮기기
@Component
@RequiredArgsConstructor
@Slf4j
public class KakaoAuthorizationCodeConfiguration {

    private final KakaoSignInProperties kakaoSignInProperties;

    private final URI kakaoGetTokenUri = URI.create("https://kauth.kakao.com/oauth/token");

    private final URI kakaoGetUserInfoUri = URI.create("https://kapi.kakao.com/v2/user/me");

    private final URI disconnectFromKakaoUri = URI.create("https://kapi.kakao.com/v1/user/unlink");

    public URI getDisconnectFromKakaoUri() {
        return this.disconnectFromKakaoUri;
    }

    public URI getKakaoGetTokenUri() {
        return this.kakaoGetTokenUri;
    }

    public URI getKakaoGetUserInfoUri() {
        return this.kakaoGetUserInfoUri;
    }

    public MultiValueMap<String, String> getAccessTokenRequestBody(String authorizationCode) {
        LinkedMultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("grant_type", "authorization_code");
        formData.add("client_id", kakaoSignInProperties.getRestApiKey());
        formData.add("redirect_uri", kakaoSignInProperties.getRedirectUri());
        formData.add("code", authorizationCode);
        formData.add("client_secret", kakaoSignInProperties.getClientSecret());

        return formData;
    }


    public MultiValueMap<String, String> getAccessTokenRequestHeader() {
        LinkedMultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE);
        return formData;
    }

    public MultiValueMap<String, String> getUserInfoRequestHeader(String accessToken) {
        LinkedMultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
        formData.add("Authorization", "Bearer " + accessToken);
        return formData;
    }

    public MultiValueMap<String, String> getDisconnectFromKakaoRequestHeader() {
        LinkedMultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("Authorization", "KakaoAK " + kakaoSignInProperties.getAdminKey());
        return formData;
    }

    public MultiValueMap<String, String> getDisconnectFromKakaoRequestBody(String kakaoUserId) {
        LinkedMultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("target_id_type","user_id");
        formData.add("target_id", kakaoUserId);
        return formData;
    }
}
