package bbangduck.bd.bbangduck.domain.member.entity;

import bbangduck.bd.bbangduck.global.config.properties.JwtSecurityProperties;
import lombok.*;

import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 작성자 : 정구민 <br><br>
 *
 * Refresh 요청을 통한 Access Token 재발급을 위해 회원 Entity 에 들어갈 Embedded 값
 */
// FIXME: 2021-05-02 Getter, Builder 를 롬복을 사용하지 않고 구현
@Embeddable
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RefreshInfo {

    private String refreshToken;

    private LocalDateTime refreshTokenExpiredDate;

    public static RefreshInfo init(int refreshTokenExpiredDate) {
        return RefreshInfo.builder()
                .refreshToken(UUID.randomUUID().toString())
                .refreshTokenExpiredDate(LocalDateTime.now().plusDays(refreshTokenExpiredDate))
                .build();
    }
}
