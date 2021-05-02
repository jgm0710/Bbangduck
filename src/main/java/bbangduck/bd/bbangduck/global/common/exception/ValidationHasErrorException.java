package bbangduck.bd.bbangduck.global.common.exception;

import bbangduck.bd.bbangduck.global.common.ResponseStatus;
import org.springframework.validation.Errors;

public class ValidationHasErrorException extends BadRequestException{

    private Errors errors;

    public ValidationHasErrorException(ResponseStatus responseStatus, Errors errors) {
        super(responseStatus);
        this.errors = errors;
    }

    public ValidationHasErrorException(ResponseStatus responseStatus, String message, Errors errors) {
        super(responseStatus, message);
        this.errors = errors;
    }

    public Errors getErrors() {
        return errors;
    }
}
