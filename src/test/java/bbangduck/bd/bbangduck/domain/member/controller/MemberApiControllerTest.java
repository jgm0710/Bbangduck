package bbangduck.bd.bbangduck.domain.member.controller;

import bbangduck.bd.bbangduck.domain.auth.dto.TokenDto;
import bbangduck.bd.bbangduck.domain.member.dto.MemberSignUpDto;
import bbangduck.bd.bbangduck.domain.member.entity.SocialType;
import bbangduck.bd.bbangduck.global.common.ResponseStatus;
import bbangduck.bd.bbangduck.member.BaseJGMApiControllerTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.ResultActions;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class MemberApiControllerTest extends BaseJGMApiControllerTest {

    @Test
    @DisplayName("회원 프로필 조회 테스트")
    public void getMemberProfileTest() throws Exception{
        //given
        MemberSignUpDto memberSignUpDto = MemberSignUpDto.builder()
                .email("test@email.com")
                .nickname("testNickname")
                .password(null)
                .socialType(SocialType.KAKAO)
                .socialId("3213123")
                .build();
        Long signUpMemberId = authenticationService.signUp(memberSignUpDto.signUp(REFRESH_TOKEN_EXPIRED_DATE));

        TokenDto tokenDto = authenticationService.signIn(signUpMemberId);
        String totalAccessToken = tokenDto.getTotalAccessToken();

        //when
        ResultActions perform = mockMvc.perform(
                get("/api/members/" + signUpMemberId)
                        .header(securityJwtProperties.getJwtTokenHeader(), totalAccessToken)
        ).andDo(print());

        //then
        perform
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("인증되지 않은 회원이 회원 프로필을 조회하는 경우")
    public void getMemberProfile_Unauthorized() throws Exception{
        //given
        MemberSignUpDto memberSignUpDto = MemberSignUpDto.builder()
                .email("test@email.com")
                .nickname("testNickname")
                .password(null)
                .socialType(SocialType.KAKAO)
                .socialId("3213123")
                .build();
        Long signUpMemberId = authenticationService.signUp(memberSignUpDto.signUp(REFRESH_TOKEN_EXPIRED_DATE));

        TokenDto tokenDto = authenticationService.signIn(signUpMemberId);
        String totalAccessToken = tokenDto.getTotalAccessToken();

        //when
        ResultActions perform = mockMvc.perform(
                get("/api/members/" + signUpMemberId)
                        .header(securityJwtProperties.getJwtTokenHeader(), totalAccessToken+"fjdiajfinwkfndwkl")
        ).andDo(print());

        //then
        perform
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("status").value(ResponseStatus.UNAUTHORIZED.getStatus()))
                .andExpect(jsonPath("message").value(ResponseStatus.UNAUTHORIZED.getMessage()))
                .andExpect(jsonPath("data").doesNotExist())
        ;
    }

    @Test
    @DisplayName("회원 조회 시 해당 회원을 찾을 수 없는 경우")
    public void getMember_NotFound() throws Exception {
        //given
        MemberSignUpDto memberSignUpDto = MemberSignUpDto.builder()
                .email("test@email.com")
                .nickname("testNickname")
                .password(null)
                .socialType(SocialType.KAKAO)
                .socialId("3213123")
                .build();
        Long signUpMemberId = authenticationService.signUp(memberSignUpDto.signUp(REFRESH_TOKEN_EXPIRED_DATE));

        TokenDto tokenDto = authenticationService.signIn(signUpMemberId);
        String totalAccessToken = tokenDto.getTotalAccessToken();

        //when
        ResultActions perform = mockMvc.perform(
                get("/api/members/" + 10000L)
                        .header(securityJwtProperties.getJwtTokenHeader(), totalAccessToken)
        ).andDo(print());


        //then
        perform
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("status").value(ResponseStatus.MEMBER_NOT_FOUND.getStatus()))
                .andExpect(jsonPath("data").doesNotExist())
                .andExpect(jsonPath("message").value(ResponseStatus.MEMBER_NOT_FOUND.getMessage()))
        ;

    }

    @Test
    @DisplayName("탈퇴한 회원이 자신의 프로필을 조회하는 경우")
    public void getMember_By_WithdrawalMember() throws Exception {
        //given
        MemberSignUpDto memberSignUpDto = MemberSignUpDto.builder()
                .email("test@email.com")
                .nickname("testNickname")
                .password(null)
                .socialType(SocialType.KAKAO)
                .socialId("3213123")
                .build();
        Long signUpMemberId = authenticationService.signUp(memberSignUpDto.signUp(REFRESH_TOKEN_EXPIRED_DATE));

        TokenDto tokenDto = authenticationService.signIn(signUpMemberId);
        String totalAccessToken = tokenDto.getTotalAccessToken();

        authenticationService.withdrawal(signUpMemberId);

        //when
        ResultActions perform = mockMvc.perform(
                get("/api/members/" + signUpMemberId)
                        .header(securityJwtProperties.getJwtTokenHeader(), totalAccessToken)
        ).andDo(print());

        //then
        perform
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("status").value(ResponseStatus.FORBIDDEN.getStatus()))
                .andExpect(jsonPath("message").value(ResponseStatus.FORBIDDEN.getMessage()))
                .andExpect(jsonPath("data").doesNotExist());

    }

    // TODO: 2021-05-05 일단 보류 추후 테스트 진행
    @Test
    @DisplayName("다른 회원의 프로필을 조회하는 경우")
    public void getMember_DifferentMember() throws Exception {
        //given
        MemberSignUpDto memberSignUpDto = MemberSignUpDto.builder()
                .email("test@email.com")
                .nickname("testNickname")
                .password(null)
                .socialType(SocialType.KAKAO)
                .socialId("3213123")
                .build();
        Long signUpMemberId = authenticationService.signUp(memberSignUpDto.signUp(REFRESH_TOKEN_EXPIRED_DATE));

        TokenDto tokenDto = authenticationService.signIn(signUpMemberId);
        String totalAccessToken = tokenDto.getTotalAccessToken();

        //when
        ResultActions perform = mockMvc.perform(
                get("/api/members/" + signUpMemberId)
                        .header(securityJwtProperties.getJwtTokenHeader(), totalAccessToken)
        ).andDo(print());

        //when

        //then

    }
}