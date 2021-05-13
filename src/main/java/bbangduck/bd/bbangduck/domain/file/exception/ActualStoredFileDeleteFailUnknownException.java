package bbangduck.bd.bbangduck.domain.file.exception;

import bbangduck.bd.bbangduck.global.common.ResponseStatus;
import bbangduck.bd.bbangduck.global.common.exception.InternalServerErrorException;

/**
 * 작성자 : 정구민 <br><br>
 *
 * 실제 저장된 파일을 삭제할 경우 알 수 없는 이유로 인해 실패했을 때 발생할 예외
 */
public class ActualStoredFileDeleteFailUnknownException extends InternalServerErrorException {
    public ActualStoredFileDeleteFailUnknownException() {
        super(ResponseStatus.FILE_DELETE_FAIL_FOR_UNKNOWN_REASON);
    }
}
