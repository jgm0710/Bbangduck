package bbangduck.bd.bbangduck.global.common.exception;

import bbangduck.bd.bbangduck.global.common.ResponseStatus;

/**
 * 작성자 : 정구민 <br><br>
 *
 * 특정 조건을 통해 Entity 인스턴스를 조회할 수 없는 경우 발생할 예외들의 최상위 Exception
 */
public abstract class NotFoundException extends StatusException {
    public NotFoundException(ResponseStatus responseStatus) {
        super(responseStatus);
    }

    public NotFoundException(ResponseStatus responseStatus, String message) {
        super(responseStatus, message);
    }

    // TODO: 2021-05-02 필요에 따라 status 와 message 를 받는 생성자 추가
}
