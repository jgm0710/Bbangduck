package bbangduck.bd.bbangduck.domain.file.exception;

import bbangduck.bd.bbangduck.global.common.ResponseStatus;
import bbangduck.bd.bbangduck.global.common.exception.BadRequestException;

/**
 * 작성자 : 정구민 <br><br>
 *
 * 파일 업로드 요청 시 제한된 파일 확장자를 가진 파일의 업로드가 요청된 경우
 * 발생할 예외
 */
public class DeniedFileExtensionException extends BadRequestException {
    public DeniedFileExtensionException() {
        super(ResponseStatus.DENIED_FILE_EXTENSION);
    }
}
