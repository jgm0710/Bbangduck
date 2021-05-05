package bbangduck.bd.bbangduck.global.common.exception;

import bbangduck.bd.bbangduck.global.common.ResponseStatus;

/**
 * 작성자 : 정구민 <br><br>
 *
 * API 요청 시 요청 Body 를 통해 들어온 값이 기존 Application 에 저장되어 있는 값과
 * 충돌될 경우 발생할 Conflict 예외를 관리하기 위한 최상위 예외
 */
public class ConflictException extends StatusException{
    public ConflictException(ResponseStatus responseStatus) {
        super(responseStatus);
    }

    public ConflictException(ResponseStatus responseStatus, String message) {
        super(responseStatus, message);
    }
}
