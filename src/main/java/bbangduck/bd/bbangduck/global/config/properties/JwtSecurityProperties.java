package bbangduck.bd.bbangduck.global.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 작성자 : 정구민 <br><br>
 *
 * JwtTokenProvider 구현 시 필요한 Properties 를 application.yml 에서 관리하기 위해 필요한 ConfigurationProperties
 */
@Component
@ConfigurationProperties("security-jwt")
@Getter
@Setter
public class JwtSecurityProperties {

    private long tokenValidSecond;
    private String secretKey;
    private int refreshTokenExpiredDate;
    private String jwtTokenHeader;

    public long getTokenValidTime() {
        return tokenValidSecond * 1000;
    }

    public long getRefreshTokenExpiredSecond() {
        return (long) refreshTokenExpiredDate * 60 * 60 * 24;
    }
}
