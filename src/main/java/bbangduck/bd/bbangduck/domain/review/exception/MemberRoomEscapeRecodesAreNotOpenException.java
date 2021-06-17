package bbangduck.bd.bbangduck.domain.review.exception;

import bbangduck.bd.bbangduck.global.common.ResponseStatus;
import bbangduck.bd.bbangduck.global.common.exception.ConflictException;

/**
 * 작성자 : Gumin Jeong
 * <p>
 * 작성 일자 : 2021-06-15
 * <p>
 * <p>
 * 다른 회원이 생성한 리뷰 목록 조회 시 해당 회원이 방탈출 기록 공개 여부를 비공개로 설정한 경우
 * 발생할 예외
 */
public class MemberRoomEscapeRecodesAreNotOpenException extends ConflictException {
    public MemberRoomEscapeRecodesAreNotOpenException() {
        super(ResponseStatus.MEMBER_ROOM_ESCAPE_RECODES_ARE_NOT_OPEN);
    }
}
