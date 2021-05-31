package bbangduck.bd.bbangduck.domain.file.exception;

import bbangduck.bd.bbangduck.global.common.ResponseStatus;
import bbangduck.bd.bbangduck.global.common.exception.BadRequestException;

/**
 * 작성자 : 정구민 <br><br>
 *
 * 이미지 파일만을 업로드하는 요청 시 해당 파일이 이미지 파일이 아닌 경우
 * 발생할 예외
 */
public class UploadFileNotImageFileException extends BadRequestException {
    public UploadFileNotImageFileException() {
        super(ResponseStatus.UPLOAD_NOT_IMAGE_FILE);
    }
}
