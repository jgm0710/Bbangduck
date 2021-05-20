package bbangduck.bd.bbangduck.domain.member.entity;

import lombok.*;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Embeddable;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 작성자 : 정구민 <br><br>
 *
 * Refresh 요청을 통한 Access Token 재발급을 위해 회원 Entity 에 들어갈 Embedded 값
 */
@Embeddable
@Access(AccessType.FIELD)
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

    @Builder
    public RefreshInfo(String refreshToken, LocalDateTime refreshTokenExpiredDate) {
        this.refreshToken = refreshToken;
        this.refreshTokenExpiredDate = refreshTokenExpiredDate;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public LocalDateTime getRefreshTokenExpiredDate() {
        return refreshTokenExpiredDate;
    }

    @Override
    public String toString() {
        return "RefreshInfo{" +
                "refreshToken='" + refreshToken + '\'' +
                ", refreshTokenExpiredDate=" + refreshTokenExpiredDate +
                '}';
    }
}
