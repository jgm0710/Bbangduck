package bbangduck.bd.bbangduck.domain.auth.exception;

import bbangduck.bd.bbangduck.global.common.ResponseStatus;
import bbangduck.bd.bbangduck.domain.auth.dto.controller.SocialAuthFailResponseAdaptorDto;

/**
 * 작성자 : 정구민 <br><br>
 *
 * 카카오 API 를 통한 유저 정보는 받아왔지만, Bbangduck Application 에 회원가입을 하지 않은 유저인 경우 발생할 예외
 */
public class KakaoAuthFailException extends SocialAuthFailException {
    public KakaoAuthFailException(SocialAuthFailResponseAdaptorDto socialAuthFailResponseAdaptorDto) {
        super(ResponseStatus.KAKAO_USER_NOT_FOUND, socialAuthFailResponseAdaptorDto);
    }
}
