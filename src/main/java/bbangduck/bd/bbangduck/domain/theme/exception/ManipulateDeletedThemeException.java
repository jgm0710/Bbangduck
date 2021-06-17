package bbangduck.bd.bbangduck.domain.theme.exception;

import bbangduck.bd.bbangduck.global.common.ResponseStatus;
import bbangduck.bd.bbangduck.global.common.exception.BadRequestException;

/**
 * 작성자 : JGM <br>
 * 작성 일자 : 2021-06-12 <br><br>
 *
 * 삭제된 테마를 사용하여 데이터 조작을 하는 경우 발생할 Exception
 */
public class ManipulateDeletedThemeException extends BadRequestException {
    public ManipulateDeletedThemeException() {
        super(ResponseStatus.MANIPULATE_DELETED_THEME);
    }
}
