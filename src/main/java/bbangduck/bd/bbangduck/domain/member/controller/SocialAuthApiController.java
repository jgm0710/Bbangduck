package bbangduck.bd.bbangduck.domain.member.controller;

import bbangduck.bd.bbangduck.domain.member.controller.status.MemberResponseStatus;
import bbangduck.bd.bbangduck.domain.member.dto.TokenDto;
import bbangduck.bd.bbangduck.domain.member.entity.Member;
import bbangduck.bd.bbangduck.domain.member.model.SocialType;
import bbangduck.bd.bbangduck.domain.member.repository.MemberQueryRepository;
import bbangduck.bd.bbangduck.domain.member.service.AuthenticationService;
import bbangduck.bd.bbangduck.domain.member.service.SocialSignInService;
import bbangduck.bd.bbangduck.global.security.social.common.dto.SocialUserInfoDto;
import bbangduck.bd.bbangduck.global.security.social.kakao.dto.KakaoOauth2TokenDto;
import bbangduck.bd.bbangduck.global.security.social.kakao.dto.KakaoUserInfoDto;
import bbangduck.bd.bbangduck.global.security.social.kakao.exception.KakaoAuthFailException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static bbangduck.bd.bbangduck.global.common.util.ModelAndViewAttributeName.*;

@RestController
@RequiredArgsConstructor
@Slf4j
public class SocialAuthApiController {

    private final SocialSignInService socialSignInService;

    private final MemberQueryRepository memberQueryRepository;

    private final AuthenticationService authenticationService;

    @GetMapping("/api/auth/kakao/sign-in")
    public void kakaoLoginRedirect(HttpServletResponse response) throws IOException {
        response.sendRedirect(socialSignInService.getKakaoAuthorizationUrl());
    }

    @GetMapping("/api/auth/kakao/sign-in/callback")
    public ModelAndView kakaoLoginCallback(
            @RequestParam("code") String code,
            @RequestParam("state") String state
    ) {

        KakaoOauth2TokenDto kakaoOauth2TokenDto = socialSignInService.getTokensFromKakao(code, state);
        log.debug("kakaoOauth2TokenDto = " + kakaoOauth2TokenDto.toString());

        KakaoUserInfoDto kakaoUserInfoDto = socialSignInService.getUserInfoFromKakao(kakaoOauth2TokenDto);
        log.debug("kakaoUserInfoDto = " + kakaoUserInfoDto);

        Member findMember = memberQueryRepository.findBySocialTypeAndSocialId(SocialType.KAKAO, kakaoUserInfoDto.getId())
                .orElseThrow(() -> new KakaoAuthFailException(SocialUserInfoDto.createSocialRegisterDto(kakaoUserInfoDto)));

        TokenDto tokenDto = authenticationService.signIn(findMember.getId());

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("social-sign-in-result");
        modelAndView.addObject(STATUS.name(), MemberResponseStatus.KAKAO_SIGN_IN_SUCCESS.getStatus());
        modelAndView.addObject(MESSAGE.name(), MemberResponseStatus.KAKAO_SIGN_IN_SUCCESS.getMessage());
        modelAndView.addObject(DATA.name(), tokenDto);

        log.info("카카오 로그인에 성공했습니다. 회원 ID : {}", findMember.getId());

        return modelAndView;

    }
}
