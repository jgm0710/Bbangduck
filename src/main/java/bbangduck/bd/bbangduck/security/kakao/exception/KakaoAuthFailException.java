package bbangduck.bd.bbangduck.security.kakao.exception;

import bbangduck.bd.bbangduck.member.social.SocialUserInfoDto;
import bbangduck.bd.bbangduck.common.ResponseStatus;
import bbangduck.bd.bbangduck.security.exception.SocialAuthFailException;

public class KakaoAuthFailException extends SocialAuthFailException {
    public KakaoAuthFailException(SocialUserInfoDto socialUserInfoDto) {
        super(ResponseStatus.KAKAO_USER_NOT_FOUND, socialUserInfoDto);
    }
}
