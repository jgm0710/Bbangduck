package bbangduck.bd.bbangduck.domain.auth.service;

import bbangduck.bd.bbangduck.domain.auth.dto.service.NaverOauth2TokenDto;
import bbangduck.bd.bbangduck.domain.auth.dto.service.NaverUserInfoDto;
import bbangduck.bd.bbangduck.domain.auth.exception.SocialAccessTokenRetrievalErrorException;
import bbangduck.bd.bbangduck.domain.auth.exception.SocialSignInStateMismatchException;
import bbangduck.bd.bbangduck.domain.auth.exception.SocialUserInfoRetrievalErrorException;
import bbangduck.bd.bbangduck.domain.member.enumerate.SocialType;
import bbangduck.bd.bbangduck.global.config.properties.NaverSignInProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

/**
 * 네이버 API 를 통한 로그인을 위한 Service
 *
 * @author jgm
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class NaverSignService {

    private final NaverSignInProperties naverSignInProperties;

    private final RestTemplate restTemplate;

    public String getNaverAuthorizationUrl() {
        String clientId = naverSignInProperties.getClientId();
        String redirectUri = naverSignInProperties.getRedirectUri();
        String authorizeState = naverSignInProperties.getAuthorizeState();
        String response_type = "code";

        return UriComponentsBuilder.fromPath("https://nid.naver.com/oauth2.0/authorize")
                .queryParam("response_type", response_type)
                .queryParam("client_id", clientId)
                .queryParam("redirect_uri", redirectUri)
                .queryParam("state", authorizeState)
                .toUriString();
    }

    public NaverUserInfoDto connectNaver(String authorizationCode, String state) {
        NaverOauth2TokenDto naverOauth2Token = getTokensFromNaver(authorizationCode, state);
        log.debug("naverOauth2Token = {}", naverOauth2Token.toString());
        NaverUserInfoDto naverUserInfo = getUserInfoFromNaver(naverOauth2Token);
        log.debug("naverUserInfo = {}", naverUserInfo.toString());

        return naverUserInfo;
    }

    public NaverOauth2TokenDto getTokensFromNaver(String authorizationCode, String state) {
        if (!state.equals(naverSignInProperties.getAuthorizeState())) {
            throw new SocialSignInStateMismatchException();
        }

        try {

            URI requestAccessTokenUri = naverSignInProperties.getRequestAccessTokenUri(authorizationCode, state);
            System.out.println("requestAccessTokenUri.toString() = " + requestAccessTokenUri.toString());
            ResponseEntity<NaverOauth2TokenDto> responseEntity = restTemplate.getForEntity(requestAccessTokenUri, NaverOauth2TokenDto.class);
//            ResponseEntity<NaverOauth2TokenDto> responseEntity = restTemplate.exchange(requestEntity, NaverOauth2TokenDto.class);

            if (responseEntity.getStatusCode().is2xxSuccessful()) {
                return responseEntity.getBody();
            } else {
                throw new SocialAccessTokenRetrievalErrorException(SocialType.NAVER);
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new SocialAccessTokenRetrievalErrorException(SocialType.NAVER);
        }
    }

    public NaverUserInfoDto getUserInfoFromNaver(NaverOauth2TokenDto naverOauth2TokenDto) {
        try {
            RequestEntity<Object> requestEntity = new RequestEntity<>(
                    naverSignInProperties.getRequestUserInfoHeader(naverOauth2TokenDto.getAccessToken()),
                    HttpMethod.POST,
                    naverSignInProperties.getRequestUserInfoHostUri()
            );

            ResponseEntity<NaverUserInfoDto> responseEntity = restTemplate.exchange(requestEntity, NaverUserInfoDto.class);

            if (responseEntity.getStatusCode().is2xxSuccessful()) {
                return responseEntity.getBody();
            } else {
                throw new SocialUserInfoRetrievalErrorException(SocialType.NAVER);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new SocialUserInfoRetrievalErrorException(SocialType.NAVER);
        }
    }
}
