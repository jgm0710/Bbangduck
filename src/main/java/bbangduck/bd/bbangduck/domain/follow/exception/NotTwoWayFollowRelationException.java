package bbangduck.bd.bbangduck.domain.follow.exception;

import bbangduck.bd.bbangduck.global.common.ResponseStatus;
import bbangduck.bd.bbangduck.global.common.exception.BadRequestException;

/**
 * 작성자 : 정구민 <br><br>
 *
 * 회원 ID 와 친구 ID 를 통해 서로 친구 관계를 맺은 기록을 조회할 수 없을 경우 발생할 예외
 */
public class NotTwoWayFollowRelationException extends BadRequestException {
    public NotTwoWayFollowRelationException(Long member1Id, Long member2Id) {
        super(ResponseStatus.NOT_TWO_WAY_FOLLOW_RELATION, ResponseStatus.NOT_TWO_WAY_FOLLOW_RELATION.getMessage() + " member1Id : " + member1Id + ", member2Id : " + member2Id);
    }
}
