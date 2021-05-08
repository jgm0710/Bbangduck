package bbangduck.bd.bbangduck.domain.file;

import bbangduck.bd.bbangduck.global.common.ResponseStatus;

public class FileStorageException extends FileUploadFailException {
    public FileStorageException(ResponseStatus responseStatus) {
        super(responseStatus);
    }

    public FileStorageException(ResponseStatus responseStatus, String message) {
        super(responseStatus, message);
    }
}
