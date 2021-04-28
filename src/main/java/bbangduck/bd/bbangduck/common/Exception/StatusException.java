package bbangduck.bd.bbangduck.common.Exception;

import bbangduck.bd.bbangduck.common.ExceptionStatus;

public class StatusException extends RuntimeException{

    private final int status;

    public StatusException(ExceptionStatus exceptionStatus) {
        super(exceptionStatus.getMessage());
        this.status = exceptionStatus.getStatus();
    }

    public StatusException(ExceptionStatus exceptionStatus, String message) {
        super(message);
        this.status = exceptionStatus.getStatus();
    }
}
