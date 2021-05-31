package bbangduck.bd.bbangduck.global.common;

import bbangduck.bd.bbangduck.global.common.exception.ValidationHasErrorException;
import org.springframework.validation.Errors;

/**
 * 작성자 : 정구민 <br><br>
 *
 * Errors hasError 등과 같이 반복적으로 예외를 처리해야 할 부분에 대해
 * 편리하게 처리하기 위해 구현한 Util Class
 */
public class ThrowUtils {
    public static void hasErrorsThrow( ResponseStatus responseStatus,Errors errors) {
        if (errors.hasErrors()) {
            throw new ValidationHasErrorException(responseStatus, errors);
        }
    }
}
