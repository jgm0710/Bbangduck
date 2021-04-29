package bbangduck.bd.bbangduck.security.kakao.exception;

import bbangduck.bd.bbangduck.common.ResponseStatus;
import bbangduck.bd.bbangduck.member.social.SocialType;
import bbangduck.bd.bbangduck.member.social.SocialUserInfoDto;
import bbangduck.bd.bbangduck.security.exception.SocialAuthFailException;

public class SocialAccessTokenRetrievalErrorException extends SocialAuthFailException {

    public SocialAccessTokenRetrievalErrorException(SocialType socialType) {
        super(
                ResponseStatus.SOCIAL_ACCESS_TOKEN_RETRIEVAL_ERROR,
                SocialUserInfoDto.builder()
                        .socialId(null)
                        .email(null)
                        .nickname(null)
                        .socialType(socialType)
                        .build()
        );
    }
}
