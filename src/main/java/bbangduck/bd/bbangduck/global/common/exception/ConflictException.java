package bbangduck.bd.bbangduck.global.common.exception;

import bbangduck.bd.bbangduck.global.common.ResponseStatus;

public class ConflictException extends StatusException{
    public ConflictException(ResponseStatus responseStatus) {
        super(responseStatus);
    }

    public ConflictException(ResponseStatus responseStatus, String message) {
        super(responseStatus, message);
    }
}
