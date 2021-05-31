package bbangduck.bd.bbangduck.domain.file.exception;

import bbangduck.bd.bbangduck.global.common.ResponseStatus;
import bbangduck.bd.bbangduck.global.common.exception.InternalServerErrorException;

/**
 * 작성자 : 정구민 <br><br>
 *
 * 알 수 없는 이유로 실제 파일 저장에 실패했을 경우 발생할 예외
 */
public class CouldNotStoreFileUnknownException extends InternalServerErrorException {
    public CouldNotStoreFileUnknownException(String fileStoredName) {
        super(ResponseStatus.COULD_NOT_STORE_FILE, ResponseStatus.COULD_NOT_STORE_FILE.getMessage() + " File Name : " + fileStoredName);
    }
}
