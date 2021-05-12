package bbangduck.bd.bbangduck.global.common.exception;

import bbangduck.bd.bbangduck.global.common.ResponseStatus;

public class EncodingUnknownException extends InternalServerErrorException{

    public EncodingUnknownException(ResponseStatus responseStatus) {
        super(responseStatus);
    }

    public EncodingUnknownException(ResponseStatus responseStatus, String message) {
        super(responseStatus, message);
    }
}
