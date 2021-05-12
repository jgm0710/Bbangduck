package bbangduck.bd.bbangduck.global.common.exception;

import bbangduck.bd.bbangduck.global.common.ResponseStatus;

/**
 * 작성자 : 정구민 <br><br>
 *
 * 예측되지 않은 문제가 발생할 경우 발생할 예외들의 최상위 예외 <br>
 * 아직 예측하지 못하는 예외가 발생 시 되도록 정해진 형식에 맞게 응답하기 위해 구현 <br>
 * InternalServerErrorException Handler 를 통해 500 응답을 하도록 하기 위해 구현
 */
public class InternalServerErrorException extends StatusException{

    public InternalServerErrorException(ResponseStatus responseStatus) {
        super(responseStatus);
    }

    public InternalServerErrorException(ResponseStatus responseStatus, String message) {
        super(responseStatus, message);
    }
}
