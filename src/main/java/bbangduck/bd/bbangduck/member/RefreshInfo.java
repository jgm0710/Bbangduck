package bbangduck.bd.bbangduck.member;

import bbangduck.bd.bbangduck.security.jwt.JwtSecurityProperties;
import lombok.*;
import org.springframework.boot.autoconfigure.security.SecurityProperties;

import javax.persistence.Embeddable;
import java.time.LocalDateTime;
import java.util.UUID;

@Embeddable
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RefreshInfo {

    private String refreshToken;

    private LocalDateTime refreshTokenExpiredDate;

    public static RefreshInfo init(JwtSecurityProperties jwtSecurityProperties) {
        return RefreshInfo.builder()
                .refreshToken(UUID.randomUUID().toString())
                .refreshTokenExpiredDate(LocalDateTime.now().plusDays(jwtSecurityProperties.getRefreshTokenExpiredDate()))
                .build();
    }
}
