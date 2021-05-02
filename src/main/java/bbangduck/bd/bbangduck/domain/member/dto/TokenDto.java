package bbangduck.bd.bbangduck.domain.member.dto;

import lombok.*;

import java.time.LocalDateTime;

/**
 * 작성자 : 정구민 <br><br>
 *
 * 로그인을 통한 인증 완료 시 응답할 Access Token, Refresh Token 의 정보를 담고 있는 Dto
 */
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TokenDto {

    private Long memberId;

    // TODO: 2021-05-02 JWT Token 을 Header, Payload, Signature 로 쪼개서 달라는 요구 있음
    private String accessToken;

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

}
