package bbangduck.bd.bbangduck.domain.auth.controller;

import bbangduck.bd.bbangduck.domain.auth.KakaoAuthorizationCodeConfiguration;
import bbangduck.bd.bbangduck.domain.auth.dto.controller.MemberSocialSignUpRequestDto;
import bbangduck.bd.bbangduck.domain.auth.dto.service.KakaoUserInfoDto;
import bbangduck.bd.bbangduck.domain.auth.dto.service.MemberSignUpDto;
import bbangduck.bd.bbangduck.domain.auth.dto.service.NaverUserInfoDto;
import bbangduck.bd.bbangduck.domain.auth.dto.service.NaverUserInfoResponseDto;
import bbangduck.bd.bbangduck.domain.auth.service.KakaoSignInService;
import bbangduck.bd.bbangduck.domain.auth.service.NaverSignService;
import bbangduck.bd.bbangduck.domain.member.enumerate.SocialType;
import bbangduck.bd.bbangduck.global.config.properties.KakaoSignInProperties;
import bbangduck.bd.bbangduck.global.config.properties.NaverSignInProperties;
import bbangduck.bd.bbangduck.member.BaseJGMApiControllerTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.ResultActions;

import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("소셜 인증 (로그인) API Controller 테스트")
@ExtendWith(MockitoExtension.class)
class SocialAuthApiControllerTest extends BaseJGMApiControllerTest {

    @MockBean
    KakaoSignInService kakaoSignInService;

    @Autowired
    KakaoAuthorizationCodeConfiguration kakaoConfiguration;

    @Autowired
    KakaoSignInProperties kakaoSignInProperties;

    @MockBean
    NaverSignService mockNaverSignInService;

    @Autowired
    NaverSignInProperties naverSignInProperties;

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

        given(kakaoSignInService.connectKakao(authorizationCode, state)).willReturn(kakaoUserInfo);

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

        given(kakaoSignInService.connectKakao(authorizationCode, state)).willReturn(kakaoUserInfo);

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

    @Test
    @DisplayName("네이버 로그인 콜백 테스트 - 회원가입이 되어있지 않은 경우")
    public void naverSignInCallback_NotSignUp() throws Exception {
        //given
        String authorizationCode = "olUb6eYQFOPkgjRANl";
        String state = naverSignInProperties.getAuthorizeState();


        NaverUserInfoResponseDto response = NaverUserInfoResponseDto.builder()
                .id("qtpa_uSX1F9auqlBfkkaTJah93f77jIuHmvMjAWka1M")
                .nickname("rnalrna****")
                .name("정구민")
                .email("rnalrnal999@naver.com")
                .gender("M")
                .age("20-29")
                .birthday("01-04")
                .profileImage("https://ssl.pstatic.net/static/pwe/address/img_profile.png")
                .birthYear("1997")
                .mobile("010-3132-3293")
                .build();

        NaverUserInfoDto naverUserInfoDto = NaverUserInfoDto.builder()
                .resultCode("00")
                .message("success")
                .response(response)
                .build();

        given(mockNaverSignInService.connectNaver(authorizationCode, state)).willReturn(naverUserInfoDto);

        //when

        //then
        mockMvc.perform(
                get("/api/auth/naver/sign-in/callback")
                        .param("code", authorizationCode)
                        .param("state", state)
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document(
                        "no-member-naver-sign-in-callback"
                ))
        ;

    }

    @Test
    @DisplayName("네이버 로그인 콜백 테스트 - 회원가입이 되어있는 경우")
    public void naverSignInCallback_SignUp() throws Exception {
        //given
        String authorizationCode = "olUb6eYQFOPkgjRANl";
        String state = naverSignInProperties.getAuthorizeState();


        String email = "rnalrnal999@naver.com";
        String nickname = "rnalrna****";
        String socialId = "qtpa_uSX1F9auqlBfkkaTJah93f77jIuHmvMjAWka1M";
        SocialType socialType = SocialType.NAVER;

        MemberSignUpDto memberSignUpDto = MemberSignUpDto.builder()
                .email(email)
                .nickname(nickname)
                .password(null)
                .socialType(socialType)
                .socialId(socialId)
                .build();

        authenticationService.signUp(memberSignUpDto);

        NaverUserInfoResponseDto response = NaverUserInfoResponseDto.builder()
                .id(socialId)
                .nickname(nickname)
                .name("정구민")
                .email(email)
                .gender("M")
                .age("20-29")
                .birthday("01-04")
                .profileImage("https://ssl.pstatic.net/static/pwe/address/img_profile.png")
                .birthYear("1997")
                .mobile("010-3132-3293")
                .build();

        NaverUserInfoDto naverUserInfoDto = NaverUserInfoDto.builder()
                .resultCode("00")
                .message("success")
                .response(response)
                .build();

        given(mockNaverSignInService.connectNaver(authorizationCode, state)).willReturn(naverUserInfoDto);

        //when

        //then
        mockMvc.perform(
                get("/api/auth/naver/sign-in/callback")
                        .param("code", authorizationCode)
                        .param("state", state)
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document(
                        "member-naver-sign-in-callback"
                ))
        ;

    }
}