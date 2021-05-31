package bbangduck.bd.bbangduck.global.common.exception;

import bbangduck.bd.bbangduck.global.common.ResponseStatus;

/**
 * 작성자 : 정구민 <br><br>
 *
 * URL 인코딩 시 알 수 없는 이유로 인코딩에 실패한 경우 발생할 예외
 */
public class URLEncodingUnknownException extends InternalServerErrorException {
    public URLEncodingUnknownException() {
        super(ResponseStatus.URL_ENCODE_ERROR);
    }
}
