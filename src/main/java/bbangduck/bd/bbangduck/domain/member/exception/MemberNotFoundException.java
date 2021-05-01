package bbangduck.bd.bbangduck.domain.member.exception;

import bbangduck.bd.bbangduck.global.common.ResponseStatus;
import bbangduck.bd.bbangduck.global.common.exception.NotFoundException;

public class MemberNotFoundException extends NotFoundException {

    public MemberNotFoundException() {
        super(ResponseStatus.MEMBER_NOT_FOUND);
    }

    public MemberNotFoundException( String email) {
        super(ResponseStatus.MEMBER_NOT_FOUND, "해당 Email 로 조회되는 회원이 존재하지 않습니다. Email : " + email);
    }
}
