package bbangduck.bd.bbangduck.domain.auth.exception;

import bbangduck.bd.bbangduck.domain.auth.dto.controller.SocialAuthFailResponseAdaptorDto;
import bbangduck.bd.bbangduck.global.common.ResponseStatus;

/**
 * 네이버 API 를 통해 로그인을 진행할 경우 네이버 API 를 통해
 * 인증 토큰 발급, 네이버 회원 정보 조회가 모두 성공했으나
 * Bbangduck 회원가입을 하지 않은 회원인 경우 발생할 예외
 *
 * @author jgm
 */
public class NaverAuthFailException extends SocialAuthFailException {
    public NaverAuthFailException(SocialAuthFailResponseAdaptorDto socialAuthFailResponseAdaptorDto) {
        super(ResponseStatus.NAVER_USER_NOT_FOUND, socialAuthFailResponseAdaptorDto);
    }
}
