package bbangduck.bd.bbangduck.domain.file.exception;

import bbangduck.bd.bbangduck.global.common.ResponseStatus;
import bbangduck.bd.bbangduck.global.common.exception.BadRequestException;

/**
 * 작성자 : 정구민 <br><br>
 *
 * 저장될 파일의 이름이 비어있는 경우 발생할 예외
 */
public class OriginalFileNameIsBlankException extends BadRequestException {
    public OriginalFileNameIsBlankException() {
        super(ResponseStatus.ORIGINAL_FILE_NAME_IS_BLANK);
    }
}
