package bbangduck.bd.bbangduck.domain.member.exception;

import bbangduck.bd.bbangduck.global.common.ResponseStatus;
import bbangduck.bd.bbangduck.global.common.exception.NotFoundException;

/**
 * 작성자 : 정구민 <br><br>
 *
 * 회원 조회 시 해당 조건을 통해 회원이 조회되지 않을 경우 발생할 예외
 */
public class MemberNotFoundException extends NotFoundException {

    public MemberNotFoundException() {
        super(ResponseStatus.MEMBER_NOT_FOUND);
    }

    public MemberNotFoundException( String email) {
        super(ResponseStatus.MEMBER_NOT_FOUND, "해당 Email 로 조회되는 회원이 존재하지 않습니다. Email : " + email);
    }
}
