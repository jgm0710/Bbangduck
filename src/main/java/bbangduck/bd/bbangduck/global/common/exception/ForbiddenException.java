package bbangduck.bd.bbangduck.global.common.exception;

import bbangduck.bd.bbangduck.global.common.ResponseStatus;

/**
 * 작성자 : 정구민 <br><br>
 *
 * 다른 회원의 리소스를 수정하거나 삭제하는 등의 경우에 발생할 최상위 예외
 */
public class ForbiddenException extends StatusException{
    public ForbiddenException(ResponseStatus responseStatus) {
        super(responseStatus);
    }

    public ForbiddenException(ResponseStatus responseStatus, String message) {
        super(responseStatus, message);
    }
}
