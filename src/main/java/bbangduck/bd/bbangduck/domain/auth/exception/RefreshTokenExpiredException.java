package bbangduck.bd.bbangduck.domain.auth.exception;

import bbangduck.bd.bbangduck.global.common.ResponseStatus;
import bbangduck.bd.bbangduck.global.common.exception.UnauthorizedException;

/**
 * 작성자 : 정구민 <br><br>
 *
 * Refresh 요청 시 Refresh Token 의 유효 기간이 만료되었을 경우 발생할 예외
 */
public class RefreshTokenExpiredException extends UnauthorizedException {
    public RefreshTokenExpiredException() {
        super(ResponseStatus.REFRESH_TOKEN_EXPIRED);
    }
}
