package bbangduck.bd.bbangduck.domain.member.exception;

import bbangduck.bd.bbangduck.global.common.ResponseStatus;
import bbangduck.bd.bbangduck.global.common.exception.BadRequestException;

/**
 * 작성자 : Gumin Jeong
 * <p>
 * 작성 일자 : 2021-06-17
 * <p>
 * 회원 조회 시 해당 회원이 탈퇴되거나, 계정이 정지된 회원일 경우 발생할 예외
 */
public class FindMemberIsWithdrawalOrBanException extends BadRequestException {
    public FindMemberIsWithdrawalOrBanException() {
        super(ResponseStatus.FIND_MEMBER_WITHDRAWAL_OR_BAN);
    }
}
