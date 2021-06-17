package bbangduck.bd.bbangduck.domain.auth.dto.service;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 작성자 : 정구민 <br><br>
 *
 * 로그인을 통한 인증 완료 시 응답할 Access Token, Refresh Token 의 정보를 담고 있는 Dto
 */
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TokenDto {

    private Long memberId;

    private String  accessToken;

    private long accessTokenValidSecond;

    private String refreshToken;

    private LocalDateTime refreshTokenExpiredDate;

    @Builder
    public TokenDto(Long memberId, String accessToken, long accessTokenValidSecond, String refreshToken, LocalDateTime refreshTokenExpiredDate) {
        this.memberId = memberId;
        this.accessToken = accessToken;
        this.accessTokenValidSecond = accessTokenValidSecond;
        this.refreshToken = refreshToken;
        this.refreshTokenExpiredDate = refreshTokenExpiredDate;
    }

    public Long getMemberId() {
        return memberId;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public long getAccessTokenValidSecond() {
        return accessTokenValidSecond;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public LocalDateTime getRefreshTokenExpiredDate() {
        return refreshTokenExpiredDate;
    }
}
