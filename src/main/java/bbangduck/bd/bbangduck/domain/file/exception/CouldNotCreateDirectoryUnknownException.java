package bbangduck.bd.bbangduck.domain.file.exception;

import bbangduck.bd.bbangduck.global.common.ResponseStatus;
import bbangduck.bd.bbangduck.global.common.exception.InternalServerErrorException;


/**
 * 작성자 : 정구민 <br><br>
 *
 * Upload Path 에 날짜별 디렉토리 생성 시 알 수 없는 이유로 디렉토리 생성에 실패한 경우
 * 발생할 예외
 */
public class CouldNotCreateDirectoryUnknownException extends InternalServerErrorException {
    public CouldNotCreateDirectoryUnknownException() {
        super(ResponseStatus.COULD_NOT_CREATE_DIRECTORY);
    }
}
