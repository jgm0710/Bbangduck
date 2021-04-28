package bbangduck.bd.bbangduck.member.social.exception;

import bbangduck.bd.bbangduck.common.ExceptionStatus;
import bbangduck.bd.bbangduck.member.social.SocialType;
import bbangduck.bd.bbangduck.member.social.SocialUserInfoDto;
import bbangduck.bd.bbangduck.security.exception.SocialUserNotFoundException;

public class SocialUserInfoRetrievalErrorException extends SocialUserNotFoundException {

    public SocialUserInfoRetrievalErrorException(SocialType socialType) {
        super(
                ExceptionStatus.SOCIAL_USER_INFO_RETRIEVAL_ERROR,
                SocialUserInfoDto.builder()
                        .socialId(null)
                        .email(null)
                        .nickname(null)
                        .socialType(socialType)
                        .build()
        );

    }
}
