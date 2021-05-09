package bbangduck.bd.bbangduck.domain.file.exception;

import bbangduck.bd.bbangduck.global.common.ResponseStatus;
import bbangduck.bd.bbangduck.global.common.exception.InternalServerErrorException;

/**
 * 작성자 : 정구민 <br><br>
 *
 * 파일을 실제 저장 시 알 수 없는 이유로 파일 저장에 실패한 경우 발생할 예외
 */
public class FileActualStorageFailUnknownException extends InternalServerErrorException {
    public FileActualStorageFailUnknownException(ResponseStatus responseStatus) {
        super(responseStatus);
    }

    public FileActualStorageFailUnknownException(ResponseStatus responseStatus, String message) {
        super(responseStatus, message);
    }
}
