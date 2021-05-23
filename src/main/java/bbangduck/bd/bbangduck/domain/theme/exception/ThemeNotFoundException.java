package bbangduck.bd.bbangduck.domain.theme.exception;

import bbangduck.bd.bbangduck.global.common.ResponseStatus;
import bbangduck.bd.bbangduck.global.common.exception.NotFoundException;


// TODO: 2021-05-23 주석 달기
public class ThemeNotFoundException extends NotFoundException {
    public ThemeNotFoundException() {
        super(ResponseStatus.THEME_NOT_FOUND);
    }
}
