package bbangduck.bd.bbangduck.domain.auth.dto.controller;

import bbangduck.bd.bbangduck.domain.auth.dto.service.TokenDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 작성자 : 정구민 <br><br>
 *
 * 로그인 요청을 통해 발급되는 Token 에 대한 응답 Data 를 담을 Dto
 * 발급된 회원의 식별 ID 를 담고 있다.
 * 발급된 JWT Access Token 을 header, payload, signature 로 나눠서 담고 있다.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TokenResponseDto {

    private Long memberId;

    private AccessJwtTokenSlicingDto accessToken;

    private long accessTokenValidSecond;

    private String refreshToken;

    private LocalDateTime refreshTokenExpiredDate;

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

    public static TokenResponseDto convert(TokenDto tokenDto) {
        return TokenResponseDto.builder()
                .memberId(tokenDto.getMemberId())
                .accessToken(new AccessJwtTokenSlicingDto(tokenDto.getAccessToken()))
                .accessTokenValidSecond(tokenDto.getAccessTokenValidSecond())
                .refreshToken(tokenDto.getRefreshToken())
                .refreshTokenExpiredDate(tokenDto.getRefreshTokenExpiredDate())
                .build();
    }
}
