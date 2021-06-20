package bbangduck.bd.bbangduck.domain.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@Component
@RequiredArgsConstructor
public class NaverSignInRequestConfiguration {

    private final NaverSignInProperties naverSignInProperties;

    public MultiValueMap<String, String> requestAccessTokenBody(String authorizationCode) {
        LinkedMultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("grant_type","authorization_code");
        formData.add("client_id", naverSignInProperties.getClientId());
        formData.add("client_secret", naverSignInProperties.getClientSecret());
        formData.add("code",authorizationCode);
        formData.add("state", naverSignInProperties.getAuthorizeState());
        return formData;
    }
}
