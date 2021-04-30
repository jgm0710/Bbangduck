package bbangduck.bd.bbangduck.domain.member.service;

import bbangduck.bd.bbangduck.domain.member.model.SocialType;
import bbangduck.bd.bbangduck.global.config.properties.KakaoLoginProperties;
import bbangduck.bd.bbangduck.global.security.social.common.exception.SocialAccessTokenRetrievalErrorException;
import bbangduck.bd.bbangduck.global.security.social.common.exception.SocialSignInStateMismatchException;
import bbangduck.bd.bbangduck.global.security.social.common.exception.SocialUserInfoRetrievalErrorException;
import bbangduck.bd.bbangduck.global.security.social.kakao.KakaoAuthorizationCodeConfiguration;
import bbangduck.bd.bbangduck.global.security.social.kakao.dto.KakaoOauth2TokenDto;
import bbangduck.bd.bbangduck.global.security.social.kakao.dto.KakaoUserInfoDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

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
                        kakaoConfiguration.getAccessTokenReqeustBody(authorizationCode),
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
                    kakaoConfiguration.getUserInfoRequestHeaher(kakaoOauth2TokenDto.getAccessToken()),
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
