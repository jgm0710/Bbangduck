package bbangduck.bd.bbangduck.api;

import bbangduck.bd.bbangduck.member.Member;
import bbangduck.bd.bbangduck.member.MemberQueryRepository;
import bbangduck.bd.bbangduck.member.social.SocialUserInfoDto;
import bbangduck.bd.bbangduck.member.social.SocialType;
import bbangduck.bd.bbangduck.security.kakao.dto.KakaoUserInfoInterfaceDto;
import bbangduck.bd.bbangduck.member.social.SocialSignInService;
import bbangduck.bd.bbangduck.member.social.exception.SocialSignInStateMismatchException;
import bbangduck.bd.bbangduck.security.kakao.KakaoOauth2TokenDto;
import bbangduck.bd.bbangduck.security.kakao.exception.KakaoUserNotFoundException;
import bbangduck.bd.bbangduck.security.kakao.exception.SocialAccessTokenRetrievalErrorException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@RequiredArgsConstructor
@Slf4j
public class SocialSignInApiController {

    private final SocialSignInService socialSignInService;

    private final MemberQueryRepository memberQueryRepository;

    @GetMapping("/api/auth/kakao/sign-in")
    public void kakaoLoginRedirect(HttpServletResponse response) throws IOException {
        response.sendRedirect(socialSignInService.getKakaoAuthorizationUrl());
    }

    @GetMapping("/api/auth/kakao/sign-in/callback")
    public ResponseEntity<KakaoOauth2TokenDto> kakaoLoginCallback(
            @RequestParam("code") String code,
            @RequestParam("state") String state
    ) {
//        if (state.equals(kakaoLoginProperties.getAuthorizeState())) {
//            KakaoOauth2TokenDto kakaoOauth2TokenDto = socialSignInService.getTokensFromKakao(code);
//            log.debug("kakaoOauth2TokenDto = " + kakaoOauth2TokenDto.toString());
//
//            return ResponseEntity.ok(kakaoOauth2TokenDto);
//        } else {
//            log.debug("Authorize state wrong.");
//            return (ResponseEntity<KakaoOauth2TokenDto>) ResponseEntity.badRequest();
//        }
        try {
            KakaoOauth2TokenDto kakaoOauth2TokenDto = socialSignInService.getTokensFromKakao(code, state);
            log.debug("kakaoOauth2TokenDto = " + kakaoOauth2TokenDto.toString());

            KakaoUserInfoInterfaceDto kakaoUserInfoDto = socialSignInService.getUserInfoFromKakao(kakaoOauth2TokenDto);
            log.debug("kakaoUserInfoDto = " + kakaoUserInfoDto);

            Member findMember = memberQueryRepository.findBySocialTypeAndSocialId(SocialType.KAKAO, kakaoUserInfoDto.getId())
                    .orElseThrow(() -> new KakaoUserNotFoundException(SocialUserInfoDto.createSocialRegisterDto(kakaoUserInfoDto)));

            return null;
        } catch (SocialSignInStateMismatchException e) {
            return null;
        } catch (SocialAccessTokenRetrievalErrorException e) {
            return null;
        }
    }
}
