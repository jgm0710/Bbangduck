package bbangduck.bd.bbangduck.member.social.exception;

import bbangduck.bd.bbangduck.common.ResponseStatus;
import bbangduck.bd.bbangduck.member.social.SocialType;
import bbangduck.bd.bbangduck.member.social.SocialUserInfoDto;
import bbangduck.bd.bbangduck.security.exception.SocialAuthFailException;

public class SocialUserInfoRetrievalErrorException extends SocialAuthFailException {

    public SocialUserInfoRetrievalErrorException(SocialType socialType) {
        super(
                ResponseStatus.SOCIAL_USER_INFO_RETRIEVAL_ERROR,
                SocialUserInfoDto.builder()
                        .socialId(null)
                        .email(null)
                        .nickname(null)
                        .socialType(socialType)
                        .build()
        );

    }
}
