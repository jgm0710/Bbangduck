package bbangduck.bd.bbangduck.security.kakao.exception;

import bbangduck.bd.bbangduck.common.ExceptionStatus;
import bbangduck.bd.bbangduck.member.social.SocialType;
import bbangduck.bd.bbangduck.member.social.SocialUserInfoDto;
import bbangduck.bd.bbangduck.security.exception.SocialUserNotFoundException;

public class SocialAccessTokenRetrievalErrorException extends SocialUserNotFoundException {

    public SocialAccessTokenRetrievalErrorException(SocialType socialType) {
        super(
                ExceptionStatus.SOCIAL_ACCESS_TOKEN_RETRIEVAL_ERROR,
                SocialUserInfoDto.builder()
                        .socialId(null)
                        .email(null)
                        .nickname(null)
                        .socialType(socialType)
                        .build()
        );
    }
}
