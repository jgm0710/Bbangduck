package bbangduck.bd.bbangduck.domain.auth.service;

import bbangduck.bd.bbangduck.domain.auth.KakaoAuthorizationCodeConfiguration;
import bbangduck.bd.bbangduck.domain.member.entity.SocialType;
import bbangduck.bd.bbangduck.global.config.properties.KakaoLoginProperties;
import bbangduck.bd.bbangduck.domain.auth.exception.SocialAccessTokenRetrievalErrorException;
import bbangduck.bd.bbangduck.domain.auth.exception.SocialSignInStateMismatchException;
import bbangduck.bd.bbangduck.domain.auth.exception.SocialUserInfoRetrievalErrorException;
import bbangduck.bd.bbangduck.domain.auth.dto.KakaoOauth2TokenDto;
import bbangduck.bd.bbangduck.domain.auth.dto.KakaoUserInfoDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

/**
 * 작성자 : 정구민 <br><br>
 *
 * 소셜 로그인 요청 시 인가 토큰 발급, 인증 토큰 발급, 소셜 회원 정보 조회 등을 처리하기 위한 Service
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SocialSignInService {

    private final KakaoAuthorizationCodeConfiguration kakaoConfiguration;

    private final KakaoLoginProperties kakaoLoginProperties;

    private final RestTemplate restTemplate;

    public String getKakaoAuthorizationUrl() {
        String clientId = kakaoLoginProperties.getRestApiKey();
        String redirectUri = kakaoLoginProperties.getRedirectUri();
        String state = kakaoLoginProperties.getAuthorizeState();
        String scope = kakaoLoginProperties.getScope();

        return "https://kauth.kakao.com/oauth/authorize?" +
                "client_id=" + clientId +
                "&redirect_uri=" + redirectUri +
                "&response_type=code" +
                "&scope=" + scope +
                "&state=" + state;
    }

    public KakaoOauth2TokenDto getTokensFromKakao(String authorizationCode, String state) {
        if (state.equals(kakaoLoginProperties.getAuthorizeState())) {
            try {
                RequestEntity<MultiValueMap<String, String>> requestEntity = new RequestEntity<>(
                        kakaoConfiguration.getAccessTokenRequestBody(authorizationCode),
                        kakaoConfiguration.getAccessTokenRequestHeader(),
                        HttpMethod.POST,
                        kakaoConfiguration.getKakaoGetTokenUri()
                );
                ResponseEntity<KakaoOauth2TokenDto> responseEntity = restTemplate.exchange(requestEntity, KakaoOauth2TokenDto.class);

                if (responseEntity.getStatusCode().is2xxSuccessful()) {
                    return responseEntity.getBody();
                } else {
                    throw new SocialAccessTokenRetrievalErrorException(SocialType.KAKAO);
                }

            } catch (Exception e) {
                e.printStackTrace();
                throw new SocialAccessTokenRetrievalErrorException(SocialType.KAKAO);
            }
        } else {
            throw new SocialSignInStateMismatchException();
        }
    }

    public KakaoUserInfoDto getUserInfoFromKakao(KakaoOauth2TokenDto kakaoOauth2TokenDto) {
        try {
            RequestEntity<Object> requestEntity = new RequestEntity<>(
                    kakaoConfiguration.getUserInfoRequestHeader(kakaoOauth2TokenDto.getAccessToken()),
                    HttpMethod.POST,
                    kakaoConfiguration.getKakaoGetUserInfoUri()
            );

            ResponseEntity<KakaoUserInfoDto> responseEntity = restTemplate.exchange(requestEntity, KakaoUserInfoDto.class);

            if (responseEntity.getStatusCode().is2xxSuccessful()) {
                return responseEntity.getBody();
            } else {
                throw new SocialUserInfoRetrievalErrorException(SocialType.KAKAO);
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new SocialUserInfoRetrievalErrorException(SocialType.KAKAO);
        }
    }
}
