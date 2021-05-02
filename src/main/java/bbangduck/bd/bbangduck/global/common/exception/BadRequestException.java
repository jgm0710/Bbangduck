package bbangduck.bd.bbangduck.global.common.exception;

import bbangduck.bd.bbangduck.global.common.ResponseStatus;

public class BadRequestException extends StatusException{
    public BadRequestException(ResponseStatus responseStatus) {
        super(responseStatus);
    }

    public BadRequestException(ResponseStatus responseStatus, String message) {
        super(responseStatus, message);
    }
}
