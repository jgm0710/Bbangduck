package bbangduck.bd.bbangduck.domain.member.exception;

import bbangduck.bd.bbangduck.domain.member.controller.status.MemberResponseStatus;
import bbangduck.bd.bbangduck.global.common.Exception.NotFoundException;

public class MemberNotFoundException extends NotFoundException {

    public MemberNotFoundException() {
        super(MemberResponseStatus.MEMBER_NOT_FOUND);
    }

    public MemberNotFoundException( String email) {
        super(MemberResponseStatus.MEMBER_NOT_FOUND, "해당 Email 로 조회되는 회원이 존재하지 않습니다. Email : " + email);
    }
}
