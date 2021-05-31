package bbangduck.bd.bbangduck.domain.auth.exception;

import bbangduck.bd.bbangduck.global.common.ResponseStatus;
import bbangduck.bd.bbangduck.global.common.exception.NotFoundException;
import bbangduck.bd.bbangduck.global.common.exception.UnauthorizedException;

/**
 * 작성자 : 정구민 <br><br>
 *
 * Refresh Token 을 통한 회원 조회가 불가능 할 경우 발생할 예외
 */
public class RefreshTokenNotFoundException extends UnauthorizedException {
    public RefreshTokenNotFoundException() {
        super(ResponseStatus.REFRESH_TOKEN_NOT_FOUND);
    }
}
