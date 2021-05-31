package bbangduck.bd.bbangduck.domain.file.exception;

import bbangduck.bd.bbangduck.global.common.ResponseStatus;
import bbangduck.bd.bbangduck.global.common.exception.InternalServerErrorException;

/**
 * 작성자 : 정구민 <br><br>
 *
 * 데이터베이스에는 파일에 대한 정보가 존재하지만, 실제 파일이 존재하지 않아
 * 파일 다운로드에 실패할 경우 발생할 예외
 */
public class ActualStoredFileNotExistException extends InternalServerErrorException {
    public ActualStoredFileNotExistException() {
        super(ResponseStatus.STORED_FILE_NOT_EXIST);
    }
}
