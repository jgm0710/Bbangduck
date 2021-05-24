package bbangduck.bd.bbangduck.domain.auth.exception;

import bbangduck.bd.bbangduck.global.common.ResponseStatus;
import bbangduck.bd.bbangduck.domain.member.entity.enumerate.SocialType;
import bbangduck.bd.bbangduck.domain.auth.controller.dto.SocialAuthFailResponseAdaptorDto;

/**
 * 작성자 : 정구민 <br><br>
 *
 * 소셜 API 를 통한 로그인 요청 시 인가 토큰을 통한 인증 토근 발급 이후
 * 소셜 회원 정보 요청 시 실패했을 경우 발생할 예외
 */
public class SocialUserInfoRetrievalErrorException extends SocialAuthFailException {

    public SocialUserInfoRetrievalErrorException(SocialType socialType) {
        super(
                ResponseStatus.SOCIAL_USER_INFO_RETRIEVAL_ERROR,
                SocialAuthFailResponseAdaptorDto.exchangeOnlySocialType(socialType)
        );
    }
}
