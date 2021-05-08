package bbangduck.bd.bbangduck.domain.file;

import bbangduck.bd.bbangduck.global.common.ResponseStatus;
import bbangduck.bd.bbangduck.global.common.exception.BadRequestException;

public class FileUploadFailException extends BadRequestException {
    public FileUploadFailException(ResponseStatus responseStatus) {
        super(responseStatus);
    }

    public FileUploadFailException(ResponseStatus responseStatus, String message) {
        super(responseStatus, message);
    }
}
