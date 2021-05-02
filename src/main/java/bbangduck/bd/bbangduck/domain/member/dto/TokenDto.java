package bbangduck.bd.bbangduck.domain.member.dto;

import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TokenDto {

    private Long memberId;

    private String accessToken;

    private long accessTokenValidSecond;

    private String refreshToken;

    private LocalDateTime refreshTokenExpiredDate;

}
