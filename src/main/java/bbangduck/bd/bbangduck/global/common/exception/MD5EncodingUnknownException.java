package bbangduck.bd.bbangduck.global.common.exception;

import bbangduck.bd.bbangduck.global.common.ResponseStatus;
import bbangduck.bd.bbangduck.global.common.exception.InternalServerErrorException;

/**
 * 작성자 : 정구민 <br><br>
 *
 * Etag 를 위한 MD5 인코딩 시 알 수 없는 이유로 인해 예외가 발생한 경우
 * 발생할 예외
 */
public class MD5EncodingUnknownException extends InternalServerErrorException {
    public MD5EncodingUnknownException() {
        super(ResponseStatus.MD5_ENCODE_ERROR);
    }
}
