package bbangduck.bd.bbangduck.domain.file.exception;

import bbangduck.bd.bbangduck.global.common.ResponseStatus;
import bbangduck.bd.bbangduck.global.common.exception.InternalServerErrorException;

/**
 * 작성자 : 정구민 <br><br>
 *
 * 실제 저장된 파일 다운로드 시 알 수 없는 이유로 파일 다운로드에 실패했을 때 발생할 예외
 */
public class ActualStoredFileDownloadFailUnknownException extends InternalServerErrorException {
    public ActualStoredFileDownloadFailUnknownException(ResponseStatus responseStatus) {
        super(responseStatus);
    }

    public ActualStoredFileDownloadFailUnknownException(ResponseStatus responseStatus, String message) {
        super(responseStatus, message);
    }
}
