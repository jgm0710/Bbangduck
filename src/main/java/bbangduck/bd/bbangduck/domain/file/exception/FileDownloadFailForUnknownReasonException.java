package bbangduck.bd.bbangduck.domain.file.exception;

import bbangduck.bd.bbangduck.global.common.ResponseStatus;
import bbangduck.bd.bbangduck.global.common.exception.InternalServerErrorException;

/**
 * 작성자 : 정구민 <br><br>
 *
 * 알 수 없는 이유로 파일 다운로드에 실패할 경우 발생할 예외
 */
public class FileDownloadFailForUnknownReasonException extends InternalServerErrorException {
    public FileDownloadFailForUnknownReasonException() {
        super(ResponseStatus.FILE_DOWNLOAD_FAIL_FOR_UNKNOWN_REASON);
    }
}
