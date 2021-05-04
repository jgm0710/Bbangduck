package bbangduck.bd.bbangduck.domain.auth.controller;

import bbangduck.bd.bbangduck.domain.auth.dto.KakaoUserInfoDto;
import bbangduck.bd.bbangduck.domain.auth.dto.SocialAuthFailResponseAdaptor;
import bbangduck.bd.bbangduck.domain.auth.exception.KakaoAuthFailException;
import bbangduck.bd.bbangduck.domain.auth.service.AuthenticationService;
import bbangduck.bd.bbangduck.domain.auth.service.SocialSignInService;
import bbangduck.bd.bbangduck.domain.auth.dto.TokenDto;
import bbangduck.bd.bbangduck.domain.member.entity.Member;
import bbangduck.bd.bbangduck.domain.member.entity.SocialType;
import bbangduck.bd.bbangduck.domain.member.repository.MemberQueryRepository;
import bbangduck.bd.bbangduck.global.common.ResponseStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static bbangduck.bd.bbangduck.global.common.ModelAndViewObjectName.*;

/**
 * 작성자 : 정구민 <br><br>
 *
 * 소셜 로그인 요청을 담당하는 Controller
 */
@RestController
@RequiredArgsConstructor
@Slf4j
public class SocialAuthApiController {

    private final SocialSignInService socialSignInService;

    private final MemberQueryRepository memberQueryRepository;

    private final AuthenticationService authenticationService;

    @GetMapping("/api/auth/kakao/sign-in")
    public void kakaoSignInRedirect(HttpServletResponse response) throws IOException {
        response.sendRedirect(socialSignInService.getKakaoAuthorizationUrl());
    }

    @GetMapping("/api/auth/kakao/sign-in/callback")
    public ModelAndView kakaoSignInCallback(
            @RequestParam("code") String code,
            @RequestParam("state") String state
    ) {
        KakaoUserInfoDto kakaoUserInfo = socialSignInService.connectKakao(code, state);

        Member findMember = memberQueryRepository.findBySocialTypeAndSocialId(SocialType.KAKAO, kakaoUserInfo.getSocialId())
                .orElseThrow(() -> new KakaoAuthFailException(SocialAuthFailResponseAdaptor.exchange(kakaoUserInfo)));

        TokenDto tokenDto = authenticationService.signIn(findMember.getId());

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("social-sign-in-result");
        modelAndView.addObject(STATUS, ResponseStatus.KAKAO_SIGN_IN_SUCCESS.getStatus());
        modelAndView.addObject(MESSAGE, ResponseStatus.KAKAO_SIGN_IN_SUCCESS.getMessage());
        modelAndView.addObject(DATA, tokenDto);

        log.info("카카오 로그인에 성공했습니다. 회원 ID : {}", findMember.getId());

        return modelAndView;

    }

    // TODO: 2021-05-02 네이버 로그인 기능 구현
}
