package bbangduck.bd.bbangduck.domain.file.exception;

import bbangduck.bd.bbangduck.global.common.ResponseStatus;
import bbangduck.bd.bbangduck.global.common.exception.NotFoundException;

/**
 * 작성자 : 정구민 <br><br>
 *
 * fileName 이나 fileId 를 통해 Database 에 저장된 파일에 대한 정보를 찾을 수 없는 경우 발생할 예외
 */
public class StoredFileNotFoundException extends NotFoundException {
    public StoredFileNotFoundException(ResponseStatus responseStatus) {
        super(responseStatus);
    }

    public StoredFileNotFoundException(ResponseStatus responseStatus, String message) {
        super(responseStatus, message);
    }
}
