package bbangduck.bd.bbangduck.domain.file.exception;

import bbangduck.bd.bbangduck.global.common.ResponseStatus;
import bbangduck.bd.bbangduck.global.common.exception.BadRequestException;

/**
 * 작성자 : 정구민 <br><br>
 *
 * 썸네일을 다운로드 받을 파일이 이미지 파일이 아닌 경우 발생할 예외
 */
public class DownloadThumbnailOfNonImageFileException extends BadRequestException {
    public DownloadThumbnailOfNonImageFileException() {
        super(ResponseStatus.DOWNLOAD_THUMBNAIL_OF_NON_IMAGE_FILE);
    }
}
