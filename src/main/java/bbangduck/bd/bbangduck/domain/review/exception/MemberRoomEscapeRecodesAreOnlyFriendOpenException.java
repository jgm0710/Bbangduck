package bbangduck.bd.bbangduck.domain.review.exception;

import bbangduck.bd.bbangduck.global.common.ResponseStatus;
import bbangduck.bd.bbangduck.global.common.exception.ConflictException;

/**
 * 작성자 : Gumin Jeong
 * <p>
 * 작성 일자 : 2021-06-16
 * <p>
 *
 * 다른 회원이 생성한 리뷰 목록을 조회할 때, 해당 회원이 방탈출 기록 공개를 친구에게만 허용했을 경우,
 * 인증된 회원과 조회되는 회원이 서로 친구가 아닐 경우 발생할 예외
 */
public class MemberRoomEscapeRecodesAreOnlyFriendOpenException extends ConflictException {
    public MemberRoomEscapeRecodesAreOnlyFriendOpenException() {
        super(ResponseStatus.MEMBER_ROOM_ESCAPE_RECODES_ARE_ONLY_FRIEND_OPEN);
    }
}
