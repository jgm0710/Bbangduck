package bbangduck.bd.bbangduck.domain.auth.controller;

import bbangduck.bd.bbangduck.domain.auth.KakaoAuthorizationCodeConfiguration;
import bbangduck.bd.bbangduck.domain.auth.service.dto.KakaoUserInfoDto;
import bbangduck.bd.bbangduck.domain.auth.service.SocialSignInService;
import bbangduck.bd.bbangduck.domain.auth.controller.dto.MemberSocialSignUpRequestDto;
import bbangduck.bd.bbangduck.domain.member.entity.SocialType;
import bbangduck.bd.bbangduck.global.config.properties.KakaoSignInProperties;
import bbangduck.bd.bbangduck.member.BaseJGMApiControllerTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.ResultActions;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class SocialAuthApiControllerTest extends BaseJGMApiControllerTest {

    @MockBean
    SocialSignInService socialSignInService;

    @Autowired
    KakaoAuthorizationCodeConfiguration kakaoConfiguration;

    @Autowired
    KakaoSignInProperties kakaoSignInProperties;

    // TODO: 2021-05-05 카카오 로그인 문서화 진행
    @Test
    @DisplayName("비회원 Kakao 로그인 콜백 테스트")
    public void Kakao_Callback_NoMember_Test() throws Exception {
        //given
        String authorizationCode = "TOKBR_LhMB_i-oYgd5pLLrMEiyPKEw7YRfTbWGQKTuql0N8eVSoKdF9dny5EyDjQ99QPrwo9c5oAAAF5Muxy7A";
        String state = kakaoSignInProperties.getAuthorizeState();

        KakaoUserInfoDto kakaoUserInfo = KakaoUserInfoDto.builder()
                .id("1698118160")
                .profileNeedsAgreement(false)
                .nickname("정구민")
                .thumbnailImageUrl("http://k.kakaocdn.net/dn/IAeHA/btq3XtLJ5z2/8eJhDt1XjRi9sLcplihygk/img_110x110.jpg")
                .profileImageUrl("http://k.kakaocdn.net/dn/IAeHA/btq3XtLJ5z2/8eJhDt1XjRi9sLcplihygk/img_640x640.jpg")
                .emailNeedsAgreement(false)
                .isEmailValid(true)
                .email("rnalrnal999@naver.com")
                .build();

        given(socialSignInService.connectKakao(authorizationCode, state)).willReturn(kakaoUserInfo);

        //when
        ResultActions perform = mockMvc.perform(
                get("/api/auth/kakao/sign-in/callback")
                        .param("code", authorizationCode)
                        .param("state", state)
        ).andDo(print());

        //then
        perform
                .andExpect(status().isOk())
                .andDo(document("no-member-kakao-sign-up-callback"))
        ;
    }

    @Test
    @DisplayName("회원 Kakao 로그인 콜백 테스트")
    public void Kakao_Callback_Member_Test() throws Exception {
        //given
        String authorizationCode = "TOKBR_LhMB_i-oYgd5pLLrMEiyPKEw7YRfTbWGQKTuql0N8eVSoKdF9dny5EyDjQ99QPrwo9c5oAAAF5Muxy7A";
        String state = kakaoSignInProperties.getAuthorizeState();

        String email = "rnalrnal999@naver.com";
        String socialId = "1698118160";
        String nickname = "정구민";

        MemberSocialSignUpRequestDto memberSocialSignUpRequestDto = MemberSocialSignUpRequestDto.builder()
                .email(email)
                .nickname(nickname)
                .socialType(SocialType.KAKAO)
                .socialId(socialId)
                .build();

        authenticationService.signUp(memberSocialSignUpRequestDto.toServiceDto());

        KakaoUserInfoDto kakaoUserInfo = KakaoUserInfoDto.builder()
                .id(socialId)
                .profileNeedsAgreement(false)
                .nickname(nickname)
                .thumbnailImageUrl("http://k.kakaocdn.net/dn/IAeHA/btq3XtLJ5z2/8eJhDt1XjRi9sLcplihygk/img_110x110.jpg")
                .profileImageUrl("http://k.kakaocdn.net/dn/IAeHA/btq3XtLJ5z2/8eJhDt1XjRi9sLcplihygk/img_640x640.jpg")
                .emailNeedsAgreement(false)
                .isEmailValid(true)
                .email(email)
                .build();

        given(socialSignInService.connectKakao(authorizationCode, state)).willReturn(kakaoUserInfo);

        //when
        ResultActions perform = mockMvc.perform(
                get("/api/auth/kakao/sign-in/callback")
                        .param("code", authorizationCode)
                        .param("state", state)
        ).andDo(print());

        //then
        perform
                .andExpect(status().isOk())
                .andDo(document("member-kakao-sign-up-callback"))
        ;
    }
}