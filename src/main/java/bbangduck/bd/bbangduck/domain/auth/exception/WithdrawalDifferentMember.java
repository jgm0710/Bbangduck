package bbangduck.bd.bbangduck.domain.auth.exception;

import bbangduck.bd.bbangduck.global.common.ResponseStatus;
import bbangduck.bd.bbangduck.global.common.exception.ForbiddenException;

/**
 * 작성자 : 정구민 <br><br>
 *
 * 회원 탈퇴 요청 시 다른 회원의 계정 탈퇴를 요청하는 경우 발생할 예외
 */
public class WithdrawalDifferentMember extends ForbiddenException {
    public WithdrawalDifferentMember() {
        super(ResponseStatus.WITHDRAWAL_DIFFERENT_MEMBER);
    }
}
