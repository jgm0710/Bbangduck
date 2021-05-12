package bbangduck.bd.bbangduck.global.common.exception;

import bbangduck.bd.bbangduck.global.common.ResponseStatus;

/**
 * 작성자 : 정구민 <br><br>
 *
 * 인코딩 시점에 알 수 없는 예외가 발생할 경우 발생할 예외
 */
public class EncodingUnknownException extends InternalServerErrorException{

    public EncodingUnknownException(ResponseStatus responseStatus) {
        super(responseStatus);
    }

    public EncodingUnknownException(ResponseStatus responseStatus, String message) {
        super(responseStatus, message);
    }
}
