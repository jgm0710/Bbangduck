package bbangduck.bd.bbangduck.global.common.exception;

import bbangduck.bd.bbangduck.global.common.ResponseStatus;

/**
 * 작성자 : 정구민 <br><br>
 *
 * AuthenticationEndPoint 에서 발생하는 Unauthorized 예외와는 별도로
 * Refresh 요청 등에서 인증이 거부될 경우 발생할 예외들의 최상위 예외
 */
public abstract class UnauthorizedException extends StatusException{
    public UnauthorizedException(ResponseStatus responseStatus) {
        super(responseStatus);
    }

    public UnauthorizedException(ResponseStatus responseStatus, String message) {
        super(responseStatus, message);
    }
}
