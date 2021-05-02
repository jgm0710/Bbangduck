package bbangduck.bd.bbangduck.global.common.exception;

import bbangduck.bd.bbangduck.global.common.ResponseStatus;

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

    public int getStatus() {
        return status;
    }
}
