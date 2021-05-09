package bbangduck.bd.bbangduck.domain.file.exception;

import bbangduck.bd.bbangduck.global.common.ResponseStatus;
import bbangduck.bd.bbangduck.global.common.exception.BadRequestException;

/**
 * 작성자 : 정구민 <br><br>
 *
 * 파일 다운로드 시 잘못된 요청을 한 경우 발생할 예외
 * 예를들면 이미지 파일이 아닌 파일의 썸네일 이미지를 요청할 경우
 */
public class StoredFileDownloadBadRequestException extends BadRequestException {
    public StoredFileDownloadBadRequestException(ResponseStatus responseStatus) {
        super(responseStatus);
    }

    public StoredFileDownloadBadRequestException(ResponseStatus responseStatus, String message) {
        super(responseStatus, message);
    }
}
