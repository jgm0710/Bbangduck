package bbangduck.bd.bbangduck.security.jwt;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

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
