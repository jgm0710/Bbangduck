package bbangduck.bd.bbangduck.domain.file.exception;

import bbangduck.bd.bbangduck.global.common.ResponseStatus;
import bbangduck.bd.bbangduck.global.common.exception.BadRequestException;

/**
 * 작성자 : 정구민 <br><br>
 *
 * 파일 업로드 요청 시 Client 가 잘 못된 요청을 보내 파일 업로드에 실패한 경우 발생할 예외
 * 파일 이름이 없는 경우, 이미지 파일 업로드 시 이미지 파일이 아닌 경우 등등
 */
public class FileUploadBadRequestException extends BadRequestException {
    public FileUploadBadRequestException(ResponseStatus responseStatus) {
        super(responseStatus);
    }

    public FileUploadBadRequestException(ResponseStatus responseStatus, String message) {
        super(responseStatus, message);
    }
}
