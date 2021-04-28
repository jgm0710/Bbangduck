package bbangduck.bd.bbangduck.common.Exception;

import bbangduck.bd.bbangduck.common.ExceptionStatus;

public class NotFoundException extends StatusException{
    public NotFoundException(ExceptionStatus exceptionStatus) {
        super(exceptionStatus);
    }

    public NotFoundException(ExceptionStatus exceptionStatus, String message) {
        super(exceptionStatus, message);
    }
}
