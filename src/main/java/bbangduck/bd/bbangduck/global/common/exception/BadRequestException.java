package bbangduck.bd.bbangduck.global.common.exception;

import bbangduck.bd.bbangduck.global.common.ResponseStatus;

/**
 * 작성자 : 정구민 <br><br>
 *
 * API 요청 시 Client 단에서 입력 값을 잘 못 기입한 경우 등에 대한
 * Client BadRequest 예외들을 관리하기 위한 최상위 Exception
 * Exception Handler 를 통해 한 번에 처리
 */
public class BadRequestException extends StatusException{
    public BadRequestException(ResponseStatus responseStatus) {
        super(responseStatus);
    }

    public BadRequestException(ResponseStatus responseStatus, String message) {
        super(responseStatus, message);
    }
}
