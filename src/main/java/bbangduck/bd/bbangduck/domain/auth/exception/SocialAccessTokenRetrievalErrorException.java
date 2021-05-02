package bbangduck.bd.bbangduck.domain.auth.exception;

import bbangduck.bd.bbangduck.global.common.ResponseStatus;
import bbangduck.bd.bbangduck.domain.member.entity.SocialType;
import bbangduck.bd.bbangduck.domain.auth.dto.SocialUserInfoDto;

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
