package bbangduck.bd.bbangduck.domain.auth.exception;

import bbangduck.bd.bbangduck.global.common.ResponseStatus;
import bbangduck.bd.bbangduck.domain.auth.dto.SocialUserInfoDto;
import bbangduck.bd.bbangduck.domain.auth.exception.SocialAuthFailException;

public class KakaoAuthFailException extends SocialAuthFailException {
    public KakaoAuthFailException(SocialUserInfoDto socialUserInfoDto) {
        super(ResponseStatus.KAKAO_USER_NOT_FOUND, socialUserInfoDto);
    }
}
