package bbangduck.bd.bbangduck.domain.member.exception;

import bbangduck.bd.bbangduck.global.common.ResponseStatus;
import bbangduck.bd.bbangduck.global.common.exception.ConflictException;

// TODO: 2021-05-16 주석
public class MemberNicknameDuplicateException extends ConflictException {
    public MemberNicknameDuplicateException(String nickname) {
        super(ResponseStatus.MEMBER_NICKNAME_DUPLICATE, ResponseStatus.MEMBER_NICKNAME_DUPLICATE.getMessage() + " Nickname : " + nickname);
    }
}
