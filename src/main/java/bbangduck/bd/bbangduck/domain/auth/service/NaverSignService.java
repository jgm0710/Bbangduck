package bbangduck.bd.bbangduck.domain.auth.service;

import bbangduck.bd.bbangduck.domain.auth.NaverSignInProperties;
import bbangduck.bd.bbangduck.domain.auth.NaverSignInRequestConfiguration;
import bbangduck.bd.bbangduck.domain.auth.dto.service.NaverOauth2TokenDto;
import bbangduck.bd.bbangduck.domain.auth.exception.SocialAccessTokenRetrievalErrorException;
import bbangduck.bd.bbangduck.domain.auth.exception.SocialSignInStateMismatchException;
import bbangduck.bd.bbangduck.domain.member.enumerate.SocialType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@RequiredArgsConstructor
@Slf4j
public class NaverSignService {

    private final NaverSignInProperties naverSignInProperties;

    private final NaverSignInRequestConfiguration naverConfiguration;

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

    // TODO: 2021-06-20 아직 미구현
    public void connectNaver(String authorizationCode, String state) {
        NaverOauth2TokenDto naverOauth2Token = getTokensFromNaver(authorizationCode, state);
        log.debug("naverOauth2Token = {}", naverOauth2Token.toString());

    }

    public NaverOauth2TokenDto getTokensFromNaver(String authorizationCode, String state) {
        if (!state.equals(naverSignInProperties.getAuthorizeState())) {
            throw new SocialSignInStateMismatchException();
        }

        try {
            RequestEntity<MultiValueMap<String, String>> requestEntity = new RequestEntity<>(
                    naverConfiguration.requestAccessTokenBody(authorizationCode),
                    HttpMethod.POST,
                    naverSignInProperties.getRequestAccessTokenHostUri()
            );
            ResponseEntity<NaverOauth2TokenDto> responseEntity = restTemplate.exchange(requestEntity, NaverOauth2TokenDto.class);

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
}
