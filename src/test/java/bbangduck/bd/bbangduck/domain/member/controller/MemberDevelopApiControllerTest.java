package bbangduck.bd.bbangduck.domain.member.controller;

import bbangduck.bd.bbangduck.domain.auth.controller.dto.MemberSignInRequestDto;
import bbangduck.bd.bbangduck.domain.auth.controller.dto.MemberSocialSignUpRequestDto;
import bbangduck.bd.bbangduck.domain.auth.service.dto.TokenDto;
import bbangduck.bd.bbangduck.domain.member.entity.Member;
import bbangduck.bd.bbangduck.domain.member.entity.enumerate.MemberRole;
import bbangduck.bd.bbangduck.domain.member.entity.enbeded.RefreshInfo;
import bbangduck.bd.bbangduck.domain.member.service.MemberDevelopService;
import bbangduck.bd.bbangduck.global.common.ResponseStatus;
import bbangduck.bd.bbangduck.member.BaseJGMApiControllerTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import java.util.Set;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class MemberDevelopApiControllerTest extends BaseJGMApiControllerTest {

    private final String email = "developer@bbangduck.com";
    private final String password = "bbangduckDEV7";

    @Autowired
    MemberDevelopService memberDevelopService;

    private TokenDto createDeveloperAccessToken() {
        MemberSignInRequestDto memberSignInRequestDto = new MemberSignInRequestDto(email, password);
        return memberDevelopService.signInDeveloper(memberSignInRequestDto.toServiceDto());
    }

    @BeforeEach
    public void setUpMemberDevelopApiControllerTest() {
        String encodedPassword = passwordEncoder.encode(password);

        if (memberRepository.findByEmail(email).isEmpty()) {
            Member member = Member.builder()
                    .email(email)
                    .password(encodedPassword)
                    .nickname("developer")
                    .description("개발자")
                    .roomEscapeRecordsOpenYN(false)
                    .refreshInfo(RefreshInfo.init(1000))
                    .roles(Set.of(MemberRole.DEVELOP, MemberRole.USER, MemberRole.ADMIN))
                    .build();

            memberRepository.save(member);
        }
    }

    @Test
    @DisplayName("개발자 권한 로그인")
    public void signInDeveloper() throws Exception {
        //given

        MemberSignInRequestDto memberSignInRequestDto = new MemberSignInRequestDto(email, password);

        //when
        ResultActions perform = mockMvc.perform(
                post("/api/develop/members/sign-in")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(memberSignInRequestDto))

        ).andDo(print());

        //then
        perform
                .andExpect(status().isOk())
                .andExpect(jsonPath("status").value(ResponseStatus.SIGN_IN_DEVELOPER_SUCCESS.getStatus()))
                .andExpect(jsonPath("data.memberId").exists())
                .andExpect(jsonPath("data.accessToken").exists())
                .andExpect(jsonPath("data.accessTokenValidSecond").exists())
                .andExpect(jsonPath("data.refreshToken").exists())
                .andExpect(jsonPath("data.refreshTokenExpiredDate").exists())
                .andExpect(jsonPath("message").value(ResponseStatus.SIGN_IN_DEVELOPER_SUCCESS.getMessage()))
                .andDo(document(
                        "sign-in-developer-success"
                ))
        ;

        mockMvc.perform(
                post("/api/develop/members/sign-in")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(memberSignInRequestDto))

        ).andDo(print()).andExpect(status().isOk());



    }

    @Test
    @DisplayName("개발자 권한으로 회원 목록 조회")
    public void getMemberListByDeveloper() throws Exception {
        //given
        MemberSocialSignUpRequestDto memberSocialSignUpRequestDto = createMemberSocialSignUpRequestDto();
        for (int i = 0; i < 10; i++) {
            memberSocialSignUpRequestDto.setEmail("test"+i+"@email.com");
            memberSocialSignUpRequestDto.setNickname("testNickname"+i);
            memberSocialSignUpRequestDto.setSocialId("33333" + i);
            authenticationService.signUp(memberSocialSignUpRequestDto.toServiceDto());
        }

        TokenDto tokenDto = createDeveloperAccessToken();

        //when
        ResultActions perform = mockMvc.perform(
                get("/api/develop/members")
                        .header(securityJwtProperties.getJwtTokenHeader(), tokenDto.getAccessToken())
                        .param("pageNum", "1")
                        .param("amount", "10")
        ).andDo(print());

        //then
        perform
                .andExpect(status().isOk())
                .andDo(document(
                        "get-member-list-by-developer-success"
                ))
        ;

    }

    @Test
    @DisplayName("개발자 권한이 아닌 회원이 회원 목록 조회")
    public void getMemberListByDeveloper_NotDeveloper() throws Exception {
        //given
        MemberSocialSignUpRequestDto memberSocialSignUpRequestDto = createMemberSocialSignUpRequestDto();
        for (int i = 0; i < 10; i++) {
            memberSocialSignUpRequestDto.setEmail("test"+i+"@email.com");
            memberSocialSignUpRequestDto.setNickname("testNickname"+i);
            memberSocialSignUpRequestDto.setSocialId("33333" + i);
            authenticationService.signUp(memberSocialSignUpRequestDto.toServiceDto());
        }

        memberSocialSignUpRequestDto.setEmail("test"+100+"@email.com");
        memberSocialSignUpRequestDto.setNickname("testNickname"+100);
        memberSocialSignUpRequestDto.setSocialId("33333" + 100);
        Long signUpId = authenticationService.signUp(memberSocialSignUpRequestDto.toServiceDto());
        TokenDto tokenDto = authenticationService.signIn(signUpId);

        //when
        ResultActions perform = mockMvc.perform(
                get("/api/develop/members")
                        .header(securityJwtProperties.getJwtTokenHeader(), tokenDto.getAccessToken())
                        .param("pageNum", "1")
                        .param("amount", "10")
        ).andDo(print());

        //then
        perform
                .andExpect(status().isForbidden());

    }

    @Test
    @DisplayName("개발자 권한으로 회원 1건 조회")
    public void getMemberByDeveloper() throws Exception {
        //given
        MemberSocialSignUpRequestDto memberSocialSignUpRequestDto = createMemberSocialSignUpRequestDto();
        Long signUpId = authenticationService.signUp(memberSocialSignUpRequestDto.toServiceDto());

        //when
        ResultActions perform = mockMvc.perform(
                get("/api/develop/members/" + signUpId)
                        .header(securityJwtProperties.getJwtTokenHeader(), createDeveloperAccessToken().getAccessToken())
        ).andDo(print());

        //then
        perform
                .andExpect(status().isOk())
                .andDo(document(
                        "get-member-by-developer-success"
                ))
        ;

    }

    @Test
    @DisplayName("개발자가 아닌 회원이 회원 1건 조회")
    public void getMemberByDeveloper_NotDeveloper() throws Exception {
        //given
        MemberSocialSignUpRequestDto memberSocialSignUpRequestDto = createMemberSocialSignUpRequestDto();
        Long signUpId = authenticationService.signUp(memberSocialSignUpRequestDto.toServiceDto());

        TokenDto tokenDto = authenticationService.signIn(signUpId);

        //when
        ResultActions perform = mockMvc.perform(
                get("/api/develop/members/" + signUpId)
                        .header(securityJwtProperties.getJwtTokenHeader(), tokenDto.getAccessToken())
        ).andDo(print());

        //then
        perform
                .andExpect(status().isForbidden());

    }

    @Test
    @DisplayName("개발자 권한으로 회원 삭제")
    public void deleteMemberByDeveloper() throws Exception {
        //given
        MemberSocialSignUpRequestDto memberSocialSignUpRequestDto = createMemberSocialSignUpRequestDto();
        Long signUpId = authenticationService.signUp(memberSocialSignUpRequestDto.toServiceDto());

        //when
        ResultActions perform = mockMvc.perform(
                delete("/api/develop/members/" + signUpId)
                        .header(securityJwtProperties.getJwtTokenHeader(), createDeveloperAccessToken().getAccessToken())
        ).andDo(print());

        //then
        perform
                .andExpect(status().isOk())
                .andDo(document(
                        "delete-member-by-developer-success"
                ))
        ;

    }

}