package bbangduck.bd.bbangduck.domain.auth.dto.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * 네이버 api 를 통한 로그인 시 인가 토큰을 통해 발급된 인증 토큰을 받기 위한 Dto
 *
 * @author jgm
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NaverOauth2TokenDto {

    @JsonProperty("access_token")
    private String accessToken;
    @JsonProperty("refresh_token")
    private String refreshToken;
    @JsonProperty("token_type")
    private String tokenType;
    @JsonProperty("expires_in")
    private Integer expiresIn;
    private String error;
    @JsonProperty("error_description")
    private String errorDescription;

}
