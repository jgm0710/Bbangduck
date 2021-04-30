package bbangduck.bd.bbangduck.global.security.social.common.exception;

public class SocialSignInStateMismatchException extends RuntimeException {
    public SocialSignInStateMismatchException() {
        super("소셜 토큰 받기 작업 중 state 값이 일치하지 않습니다.");
    }
}
