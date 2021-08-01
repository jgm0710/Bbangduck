package bbangduck.bd.bbangduck.domain.auth.exception;

import bbangduck.bd.bbangduck.domain.member.enumerate.SocialType;
import bbangduck.bd.bbangduck.global.common.ResponseStatus;
import bbangduck.bd.bbangduck.global.common.exception.InternalServerErrorException;

/**
 * 카카오 계정 연동 해제 요청에 실패한 경우 발생할 예외
 *
 * @author Gumin Jeong
 * @since 2021-07-24
 */
public class SocialAccountDisconnectFailException extends InternalServerErrorException {
    public SocialAccountDisconnectFailException(SocialType socialType) {
        super(ResponseStatus.SOCIAL_ACCOUNT_DISCONNECT_FAIL, ResponseStatus.SOCIAL_ACCOUNT_DISCONNECT_FAIL.getMessage() + " SocialType : " + socialType);
    }
}
