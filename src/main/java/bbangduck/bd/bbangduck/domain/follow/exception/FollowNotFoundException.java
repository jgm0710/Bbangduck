package bbangduck.bd.bbangduck.domain.follow.exception;

import bbangduck.bd.bbangduck.global.common.ResponseStatus;
import bbangduck.bd.bbangduck.global.common.exception.NotFoundException;

/**
 * 팔로우 내역 조회 시 팔로우 내역을 찾을 수 없는 경우 발생할 예외
 *
 * @author Gumin Jeong
 * @since 2021-07-17
 */
public class FollowNotFoundException extends NotFoundException {
    public FollowNotFoundException() {
        super(ResponseStatus.FOLLOW_NOT_FOUND);
    }
}
