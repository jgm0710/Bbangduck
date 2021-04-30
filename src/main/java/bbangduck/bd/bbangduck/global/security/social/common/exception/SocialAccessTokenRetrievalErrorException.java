package bbangduck.bd.bbangduck.global.security.social.common.exception;

import bbangduck.bd.bbangduck.domain.member.controller.status.MemberResponseStatus;
import bbangduck.bd.bbangduck.domain.member.model.SocialType;
import bbangduck.bd.bbangduck.global.security.social.common.dto.SocialUserInfoDto;

public class SocialAccessTokenRetrievalErrorException extends SocialAuthFailException {

    public SocialAccessTokenRetrievalErrorException(SocialType socialType) {
        super(
                MemberResponseStatus.SOCIAL_ACCESS_TOKEN_RETRIEVAL_ERROR,
                SocialUserInfoDto.builder()
                        .socialId(null)
                        .email(null)
                        .nickname(null)
                        .socialType(socialType)
                        .build()
        );
    }
}
