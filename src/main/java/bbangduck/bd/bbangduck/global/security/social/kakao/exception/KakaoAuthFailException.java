package bbangduck.bd.bbangduck.global.security.social.kakao.exception;

import bbangduck.bd.bbangduck.domain.member.controller.status.MemberResponseStatus;
import bbangduck.bd.bbangduck.global.security.social.common.dto.SocialUserInfoDto;
import bbangduck.bd.bbangduck.global.security.social.common.exception.SocialAuthFailException;

public class KakaoAuthFailException extends SocialAuthFailException {
    public KakaoAuthFailException(SocialUserInfoDto socialUserInfoDto) {
        super(MemberResponseStatus.KAKAO_USER_NOT_FOUND, socialUserInfoDto);
    }
}
