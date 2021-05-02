package bbangduck.bd.bbangduck.global.common.exception;

import bbangduck.bd.bbangduck.global.common.ResponseStatus;

public class NotFoundException extends StatusException {
    public NotFoundException(ResponseStatus responseStatus) {
        super(responseStatus);
    }

    public NotFoundException(ResponseStatus responseStatus, String message) {
        super(responseStatus, message);
    }
}
