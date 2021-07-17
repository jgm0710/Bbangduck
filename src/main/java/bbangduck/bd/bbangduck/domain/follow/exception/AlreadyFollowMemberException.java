package bbangduck.bd.bbangduck.domain.follow.exception;

import bbangduck.bd.bbangduck.global.common.ResponseStatus;
import bbangduck.bd.bbangduck.global.common.exception.BadRequestException;

/**
 * 이미 팔로우 요청한 회원에게 다시 팔로우를 요청하는 경우 발생할 예외
 *
 * @author Gumin Jeong
 * @since 2021-07-17
 */
public class AlreadyFollowMemberException extends BadRequestException {
    public AlreadyFollowMemberException() {
        super(ResponseStatus.ALREADY_FOLLOW_MEMBER);
    }
}
