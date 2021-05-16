package bbangduck.bd.bbangduck.domain.member.exception;

import bbangduck.bd.bbangduck.domain.member.entity.SocialType;
import bbangduck.bd.bbangduck.global.common.ResponseStatus;
import bbangduck.bd.bbangduck.global.common.exception.ConflictException;

// TODO: 2021-05-16 주석
public class MemberSocialInfoDuplicateException extends ConflictException {
    public MemberSocialInfoDuplicateException(SocialType socialType, String socialId) {
        super(ResponseStatus.MEMBER_SOCIAL_INFO_DUPLICATE, ResponseStatus.MEMBER_SOCIAL_INFO_DUPLICATE.getMessage() + " SocialType : " + socialType + " , SocialId : " + socialId);
    }
}
