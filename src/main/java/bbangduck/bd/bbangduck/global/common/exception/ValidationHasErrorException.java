package bbangduck.bd.bbangduck.global.common.exception;

import bbangduck.bd.bbangduck.global.common.ResponseStatus;
import org.springframework.validation.Errors;

/**
 * 작성자 : 정구민 <br><br>
 *
 * API 요청 시 Client 가 기입한 요청 Body 의 값이 해당 Api 의 스펙과 맞지 않을 경우 발생할 예외
 * BadRequestException 을 상속 받아 별도로 관리
 */
public class ValidationHasErrorException extends BadRequestException{

    private Errors errors;

    public ValidationHasErrorException(ResponseStatus responseStatus, Errors errors) {
        super(responseStatus);
        this.errors = errors;
    }

    public ValidationHasErrorException(ResponseStatus responseStatus, String message, Errors errors) {
        super(responseStatus, message);
        this.errors = errors;
    }

    public Errors getErrors() {
        return errors;
    }
}
