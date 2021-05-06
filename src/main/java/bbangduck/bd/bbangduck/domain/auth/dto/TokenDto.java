package bbangduck.bd.bbangduck.domain.auth.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 작성자 : 정구민 <br><br>
 *
 * 로그인을 통한 인증 완료 시 응답할 Access Token, Refresh Token 의 정보를 담고 있는 Dto
 */
@Data
@NoArgsConstructor
public class TokenDto {

    private Long memberId;

    private AccessJwtTokenSlicingDto accessToken;

    private long accessTokenValidSecond;

    private String refreshToken;

    private LocalDateTime refreshTokenExpiredDate;

    @Builder
    public TokenDto(Long memberId, String accessToken, long accessTokenValidSecond, String refreshToken, LocalDateTime refreshTokenExpiredDate) {
        this.memberId = memberId;
        this.accessToken = new AccessJwtTokenSlicingDto(accessToken);
        this.accessTokenValidSecond = accessTokenValidSecond;
        this.refreshToken = refreshToken;
        this.refreshTokenExpiredDate = refreshTokenExpiredDate;
    }

    @JsonIgnore
    public String getTotalAccessToken() {
        return this.accessToken.getHeader() + "." + this.accessToken.getPayload() + "." + this.accessToken.getSignature();
    }

    @Data
    static class AccessJwtTokenSlicingDto {
        private String header;
        private String payload;
        private String signature;

        public AccessJwtTokenSlicingDto(String accessToken) {
            int i = accessToken.indexOf('.');
            String header = accessToken.substring(0,i);
            accessToken = accessToken.substring(i + 1);
            int i1 = accessToken.indexOf('.');
            String payload = accessToken.substring(0, i1);
            String signature = accessToken.substring(i1 + 1);

            this.header = header;
            this.payload = payload;
            this.signature = signature;
        }
    }

}
