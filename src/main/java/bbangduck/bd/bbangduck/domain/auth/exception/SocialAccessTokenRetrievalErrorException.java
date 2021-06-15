package bbangduck.bd.bbangduck.domain.auth.exception;

import bbangduck.bd.bbangduck.global.common.ResponseStatus;
import bbangduck.bd.bbangduck.domain.member.entity.enumerate.SocialType;
import bbangduck.bd.bbangduck.domain.auth.dto.controller.SocialAuthFailResponseAdaptorDto;

/**
 * 작성자 : 정구민 <br><br>
 *
 * 소셜 API 를 통한 Access Token 요청 중, Authorization Code 를 통한 Access Token 응답에 실패한 경우 발생할 예외
 */
public class SocialAccessTokenRetrievalErrorException extends SocialAuthFailException {
    public SocialAccessTokenRetrievalErrorException(SocialType socialType) {
        super(
                ResponseStatus.SOCIAL_ACCESS_TOKEN_RETRIEVAL_ERROR,
                SocialAuthFailResponseAdaptorDto.exchangeOnlySocialType(socialType)
        );
    }
}
