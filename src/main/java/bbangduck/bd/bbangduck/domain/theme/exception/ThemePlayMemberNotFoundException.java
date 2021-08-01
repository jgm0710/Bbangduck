package bbangduck.bd.bbangduck.domain.theme.exception;

import bbangduck.bd.bbangduck.global.common.ResponseStatus;
import bbangduck.bd.bbangduck.global.common.exception.NotFoundException;

/**
 * 회원이 테마를 플레이한 내역을 조회할 경우 해당 내역이 존재하지 않을 때 발생할 예외
 *
 * @author Gumin Jeong
 * @since 2021-07-22
 */
public class ThemePlayMemberNotFoundException extends NotFoundException {
    public ThemePlayMemberNotFoundException() {
        super(ResponseStatus.THEME_PLAY_MEMBER_NOT_FOUND);
    }
}
