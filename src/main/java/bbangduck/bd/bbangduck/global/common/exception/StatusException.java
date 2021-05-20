package bbangduck.bd.bbangduck.global.common.exception;

import bbangduck.bd.bbangduck.global.common.ResponseStatus;

/**
 * 작성자 : 정구민 <br><br>
 *
 * ResponseDto 를 통해 HttpStatus 외에 상태값을 응답하기 위해 구현한 최상위 Exception
 */
public abstract class StatusException extends RuntimeException{

    private final int status;

    public StatusException(ResponseStatus responseStatus) {
        super(responseStatus.getMessage());
        this.status = responseStatus.getStatus();
    }

    public StatusException(ResponseStatus responseStatus, String message) {
        super(message);
        this.status = responseStatus.getStatus();
    }

    // TODO: 2021-05-02 필요에 따라 status 와 message 를 받는 생성자 추가

    public int getStatus() {
        return status;
    }
}
