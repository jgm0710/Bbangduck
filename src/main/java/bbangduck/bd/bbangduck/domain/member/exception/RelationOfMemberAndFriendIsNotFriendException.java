package bbangduck.bd.bbangduck.domain.member.exception;

import bbangduck.bd.bbangduck.global.common.ResponseStatus;
import bbangduck.bd.bbangduck.global.common.exception.BadRequestException;

/**
 * 작성자 : 정구민 <br><br>
 *
 * 회원 ID 와 친구 ID 를 통해 서로 친구 관계를 맺은 기록을 조회할 수 없을 경우 발생할 예외
 */
public class RelationOfMemberAndFriendIsNotFriendException extends BadRequestException {
    public RelationOfMemberAndFriendIsNotFriendException(Long memberId, Long friendId) {
        super(ResponseStatus.RELATION_OF_MEMBER_AND_FRIEND_IS_NOT_FRIEND, ResponseStatus.RELATION_OF_MEMBER_AND_FRIEND_IS_NOT_FRIEND.getMessage() + " MemberId : " + memberId + ", FriendId : " + friendId);
    }
}
