package bbangduck.bd.bbangduck.global.common.exception;

import bbangduck.bd.bbangduck.global.common.ResponseStatus;

public class InternalServerErrorException extends StatusException{

    public InternalServerErrorException(ResponseStatus responseStatus) {
        super(responseStatus);
    }

    public InternalServerErrorException(ResponseStatus responseStatus, String message) {
        super(responseStatus, message);
    }
}
