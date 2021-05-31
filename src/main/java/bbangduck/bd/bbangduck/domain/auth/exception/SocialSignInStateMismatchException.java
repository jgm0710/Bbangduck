package bbangduck.bd.bbangduck.domain.auth.exception;

import bbangduck.bd.bbangduck.global.common.ResponseStatus;

/**
 * 작성자 : 정구민 <br><br>
 *
 * 소셜 로그인 요청 시 CSRF 를 방지하기 위한 요청 state 값이 응답 state 값과 다른 경우 발생할 예외
 */
public class SocialSignInStateMismatchException extends SocialAuthFailException {
    public SocialSignInStateMismatchException() {
        super(ResponseStatus.SOCIAL_SIGN_IN_STATE_MISMATCH, null);
    }
}
