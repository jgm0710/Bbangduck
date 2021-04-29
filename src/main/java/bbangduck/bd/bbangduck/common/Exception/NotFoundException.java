package bbangduck.bd.bbangduck.common.Exception;

import bbangduck.bd.bbangduck.common.ResponseStatus;

public class NotFoundException extends StatusException{
    public NotFoundException(ResponseStatus responseStatus) {
        super(responseStatus);
    }

    public NotFoundException(ResponseStatus responseStatus, String message) {
        super(responseStatus, message);
    }
}
