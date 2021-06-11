package bbangduck.bd.bbangduck.domain.theme.exception;

import bbangduck.bd.bbangduck.global.common.ResponseStatus;
import bbangduck.bd.bbangduck.global.common.exception.NotFoundException;


/**
 * 작성자 : 정구민 <br><br>
 *
 * 테마 조회 시 테마를 찾을 수 없을 경우 발생할 예외
 */
public class ThemeNotFoundException extends NotFoundException {
    public ThemeNotFoundException() {
        super(ResponseStatus.THEME_NOT_FOUND);
    }
}
