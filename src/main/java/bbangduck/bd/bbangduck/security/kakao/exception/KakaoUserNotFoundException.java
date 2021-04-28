package bbangduck.bd.bbangduck.security.kakao.exception;

import bbangduck.bd.bbangduck.member.social.SocialUserInfoDto;
import bbangduck.bd.bbangduck.member.social.SocialType;
import bbangduck.bd.bbangduck.security.exception.ExceptionStatus;
import bbangduck.bd.bbangduck.security.exception.SocialUserNotFoundException;

public class KakaoUserNotFoundException extends SocialUserNotFoundException {
    public KakaoUserNotFoundException(SocialUserInfoDto socialUserInfoDto) {
        super(ExceptionStatus.KAKAO_USER_NOT_FOUND, socialUserInfoDto, "kakao_user_info");
    }
}
