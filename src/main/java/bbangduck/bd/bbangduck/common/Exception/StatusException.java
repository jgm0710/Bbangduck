package bbangduck.bd.bbangduck.common.Exception;

import bbangduck.bd.bbangduck.common.ResponseStatus;

public class StatusException extends RuntimeException{

    private final int status;

    public StatusException(ResponseStatus responseStatus) {
        super(responseStatus.getMessage());
        this.status = responseStatus.getStatus();
    }

    public StatusException(ResponseStatus responseStatus, String message) {
        super(message);
        this.status = responseStatus.getStatus();
    }
}
