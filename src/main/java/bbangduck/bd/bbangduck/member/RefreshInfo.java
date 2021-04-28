package bbangduck.bd.bbangduck.member;

import javax.persistence.Embeddable;
import java.time.LocalDateTime;

@Embeddable
public class RefreshInfo {

    private String refreshToken;

    private LocalDateTime refreshTokenExpiredDate;
}
