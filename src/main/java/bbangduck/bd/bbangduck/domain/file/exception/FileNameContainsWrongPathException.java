package bbangduck.bd.bbangduck.domain.file.exception;

import bbangduck.bd.bbangduck.global.common.ResponseStatus;
import bbangduck.bd.bbangduck.global.common.exception.BadRequestException;

/**
 * 작성자 : 정구민 <br><br>
 *
 * 파일의 이름에 잘못된 경로가 포함된 경우 발생할 예외
 */
public class FileNameContainsWrongPathException extends BadRequestException {
    public FileNameContainsWrongPathException(String fileStoredName) {
        super(ResponseStatus.FILE_NAME_CONTAINS_WRONG_PATH, ResponseStatus.FILE_NAME_CONTAINS_WRONG_PATH.getMessage() + " File Name : " + fileStoredName);
    }
}
