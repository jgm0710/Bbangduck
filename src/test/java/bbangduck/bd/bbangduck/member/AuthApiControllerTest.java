package bbangduck.bd.bbangduck.member;

import bbangduck.bd.bbangduck.domain.auth.controller.dto.OnlyRefreshTokenRequestDto;
import bbangduck.bd.bbangduck.domain.auth.service.dto.TokenDto;
import bbangduck.bd.bbangduck.domain.auth.controller.dto.MemberSocialSignUpRequestDto;
import bbangduck.bd.bbangduck.domain.auth.service.dto.MemberSignUpDto;
import bbangduck.bd.bbangduck.domain.member.entity.Member;
import bbangduck.bd.bbangduck.domain.member.entity.MemberRole;
import bbangduck.bd.bbangduck.domain.member.entity.RefreshInfo;
import bbangduck.bd.bbangduck.domain.member.entity.SocialType;
import bbangduck.bd.bbangduck.domain.member.exception.MemberEmailDuplicateException;
import bbangduck.bd.bbangduck.domain.member.exception.MemberNicknameDuplicateException;
import bbangduck.bd.bbangduck.domain.member.exception.MemberSocialInfoDuplicateException;
import bbangduck.bd.bbangduck.global.common.ResponseStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

import static bbangduck.bd.bbangduck.global.common.ResponseStatus.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class AuthApiControllerTest extends BaseJGMApiControllerTest {

    @Test
    @DisplayName("소셜 회원가입 테스트")
    public void signUpTest() throws Exception {
        //given
        MemberSocialSignUpRequestDto memberSocialSignUpRequestDto = MemberSocialSignUpRequestDto.builder()
                .email("test@email.com")
                .nickname("testNickname")

                .socialType(SocialType.KAKAO)
                .socialId("321312")
                .build();

        //when
        ResultActions perform = mockMvc.perform(
                post("/api/auth/social/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(memberSocialSignUpRequestDto))
        ).andDo(print());

        //then
        perform
                .andExpect(status().isCreated())
                .andDo(document(
                        "social-sign-up-success",
                        requestHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("[application/json;charset=UTF-8] 로 지정")
                        ),
                        requestFields(
                                fieldWithPath("email").description("회원 식별에 필요한 Email 기입"),
                                fieldWithPath("nickname").description("회원 활동 시 필요한 Nickname 기입"),
                                fieldWithPath("socialType").description("Social 인증을 통해 회원가입을 진행하는 경우 어떠한 Social 매체를 사용하여 회원가입을 진행하는 지 명시"),
                                fieldWithPath("socialId").description("Social 인증을 통해 회원가입을 진행하는 경우 해당 Social 매체 내에서 회원을 식별하기 위해 사용하는 ID 값을 기입 + \n" +
                                        "해당 SocialId 를 통해 회원을 식별합니다.")
                        ),
                        responseFields(
                                fieldWithPath("status").description(STATUS_DESCRIPTION),
                                fieldWithPath("data.memberInfo.memberId").description("가입된 회원의 식별 ID"),
                                fieldWithPath("data.memberInfo.email").description("가입된 회원의 Email"),
                                fieldWithPath("data.memberInfo.nickname").description("가입된 회원의 Nickname"),
                                fieldWithPath("data.memberInfo.profileImage").description("가입된 회원의 프로필 이미지 (null)"),
                                fieldWithPath("data.memberInfo.socialAccounts[0].socialId").description("Social 인증을 통해 가입된 회원의 Social ID"),
                                fieldWithPath("data.memberInfo.socialAccounts[0].socialType").description("Social 인증을 통해 가입된 회원이 사용한 Social 매체"),
                                fieldWithPath("data.memberInfo.description").description("간략한 자기 소개 (null)"),
                                fieldWithPath("data.memberInfo.reviewCount").description("작성한 리뷰 수 (null)"),
                                fieldWithPath("data.memberInfo.roomEscapeRecordVisible").description("방탈출 기록 공개 여부 (Default True)"),
                                fieldWithPath("data.memberInfo.registerDate").description("가입 날짜"),
                                fieldWithPath("data.memberInfo.updateDate").description("회원 정보 최종 수정 날짜"),
                                fieldWithPath("data.tokenInfo.memberId").description("가입된 회원의 식별 ID"),
                                fieldWithPath("data.tokenInfo.accessToken.header").description("가입된 회원에게 발급된 인증 JWT 토큰의 Header"),
                                fieldWithPath("data.tokenInfo.accessToken.payload").description("가입된 회원에게 발급된 인증 JWT 토큰의 Payload"),
                                fieldWithPath("data.tokenInfo.accessToken.signature").description("가입된 회원에게 발급된 인증 JWT 토큰의 Signature"),
                                fieldWithPath("data.tokenInfo.accessTokenValidSecond").description("가입된 회원에게 발급된 인증 JWT 토큰이 만료되기까지 남은 시간"),
                                fieldWithPath("data.tokenInfo.refreshToken").description("인증토큰 재발급 요청에 필요한 Refresh 토큰"),
                                fieldWithPath("data.tokenInfo.refreshTokenExpiredDate").description("인증토큰 재발급 요청에 필요한 Refresh 토큰의 만료 날짜"),
                                fieldWithPath("message").description(MESSAGE_DESCRIPTION)
                        )
                ))
        ;

    }

    @Test
    @DisplayName("회원가입 이메일 중복 테스트")
    public void signUp_EmailDuplicate() throws Exception {
        //given
        MemberSocialSignUpRequestDto memberSocialSignUpRequestDto = MemberSocialSignUpRequestDto.builder()
                .email("test@email.com")
                .nickname("testNickname")

                .socialType(SocialType.KAKAO)
                .socialId("321312")
                .build();

        authenticationService.signUp(memberSocialSignUpRequestDto.toServiceDto());


        MemberSocialSignUpRequestDto memberSocialSignUpRequestDto2 = MemberSocialSignUpRequestDto.builder()
                .email("test@email.com")
                .nickname("testNickname2")
                .socialType(SocialType.KAKAO)
                .socialId("321312")
                .build();


        //when
        ResultActions perform = mockMvc.perform(
                post("/api/auth/social/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(memberSocialSignUpRequestDto2))
        ).andDo(print());

        //then
        perform
                .andExpect(status().isConflict())
                .andExpect(jsonPath("status").value(MEMBER_EMAIL_DUPLICATE.getStatus()))
                .andExpect(jsonPath("data").doesNotExist())
                .andExpect(jsonPath("message").value(new MemberEmailDuplicateException(memberSocialSignUpRequestDto2.getEmail()).getMessage()))
        ;

    }

    @Test
    @DisplayName("회원가입 닉네임 중복 테스트")
    public void signUp_NicknameDuplicate() throws Exception {
        //given
        MemberSocialSignUpRequestDto memberSocialSignUpRequestDto = MemberSocialSignUpRequestDto.builder()
                .email("test@email.com")
                .nickname("testNickname")

                .socialType(SocialType.KAKAO)
                .socialId("321312")
                .build();

        authenticationService.signUp(memberSocialSignUpRequestDto.toServiceDto());


        MemberSocialSignUpRequestDto memberSocialSignUpRequestDto2 = MemberSocialSignUpRequestDto.builder()
                .email("test@email.com2")
                .nickname("testNickname")
                .socialType(SocialType.KAKAO)
                .socialId("312331312")
                .build();

        //when
        ResultActions perform = mockMvc.perform(
                post("/api/auth/social/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(memberSocialSignUpRequestDto2))
        ).andDo(print());

        //then
        perform
                .andExpect(status().isConflict())
                .andExpect(jsonPath("status").value(MEMBER_NICKNAME_DUPLICATE.getStatus()))
                .andExpect(jsonPath("data").doesNotExist())
                .andExpect(jsonPath("message").value(new MemberNicknameDuplicateException(memberSocialSignUpRequestDto2.getNickname()).getMessage()))
        ;

    }

    @Test
    @DisplayName("소셜 회원가입 시 소셜 정보 중복 테스트")
    public void signUp_SocialInfoDuplicate() throws Exception {
        //given
        MemberSocialSignUpRequestDto memberSocialSignUpRequestDto = MemberSocialSignUpRequestDto.builder()
                .email("test@email.com")
                .nickname("testNickname")

                .socialType(SocialType.KAKAO)
                .socialId("321312")
                .build();

        authenticationService.signUp(memberSocialSignUpRequestDto.toServiceDto());


        MemberSocialSignUpRequestDto memberSocialSignUpRequestDto2 = MemberSocialSignUpRequestDto.builder()
                .email("test2@email.com")
                .nickname("testNickname2")

                .socialType(SocialType.KAKAO)
                .socialId("321312")
                .build();

        //when
        ResultActions perform = mockMvc.perform(
                post("/api/auth/social/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(memberSocialSignUpRequestDto2))
        ).andDo(print());

        //then
        perform
                .andExpect(status().isConflict())
                .andExpect(jsonPath("status").value(MEMBER_SOCIAL_INFO_DUPLICATE.getStatus()))
                .andExpect(jsonPath("data").doesNotExist())
                .andExpect(jsonPath("message")
                        .value(new MemberSocialInfoDuplicateException(memberSocialSignUpRequestDto2.getSocialType(), memberSocialSignUpRequestDto2.getSocialId()).getMessage()))
        ;

    }

    @Test
    @DisplayName("소셜 회원가입 시 이메일을 기입하지 않은 경우")
    public void signUpTest_EmailEmpty() throws Exception {
        //given
        MemberSocialSignUpRequestDto memberSocialSignUpRequestDto = MemberSocialSignUpRequestDto.builder()
                .email("")
                .nickname("testNickname")

                .socialType(SocialType.KAKAO)
                .socialId("321312")
                .build();


        //when
        ResultActions perform = mockMvc.perform(
                post("/api/auth/social/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(memberSocialSignUpRequestDto))
        ).andDo(print());

        //then
        perform
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("data[0].code").value("NotBlank"))
        ;
    }

    @Test
    @DisplayName("소셜 회원가입 시 닉네임을 기입하지 않은 경우")
    public void signUpTest_NicknameEmpty() throws Exception {
        //given
        MemberSocialSignUpRequestDto memberSocialSignUpRequestDto = MemberSocialSignUpRequestDto.builder()
                .email("test@email.com")
                .nickname("")

                .socialType(SocialType.KAKAO)
                .socialId("321312")
                .build();


        //when
        ResultActions perform = mockMvc.perform(
                post("/api/auth/social/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(memberSocialSignUpRequestDto))
        ).andDo(print());

        //then
        perform
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("data[0].code").value("NotBlank"))
        ;
    }

    @Test
    @DisplayName("소셜 회원가입 시 소셜 ID 를 기입하지 않은 경우")
    public void signUpTest_SocialIdEmpty() throws Exception {
        //given
        MemberSocialSignUpRequestDto memberSocialSignUpRequestDto = MemberSocialSignUpRequestDto.builder()
                .email("test@email.com")
                .nickname("test")

                .socialType(SocialType.KAKAO)
                .socialId(null)
                .build();


        //when
        ResultActions perform = mockMvc.perform(
                post("/api/auth/social/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(memberSocialSignUpRequestDto))
        ).andDo(print());

        //then
        perform
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("data[0].code").value("NotBlank"))
        ;
    }

    @Test
    @DisplayName("소셜 회원가입 시 소셜 타입을 기입하지 않은 경우")
    public void signUpTest_SocialTypeEmpty() throws Exception {
        //given
        MemberSocialSignUpRequestDto memberSocialSignUpRequestDto = MemberSocialSignUpRequestDto.builder()
                .email("test@email.com")
                .nickname("test")
                .socialType(null)
                .socialId("321312")
                .build();


        //when
        ResultActions perform = mockMvc.perform(
                post("/api/auth/social/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(memberSocialSignUpRequestDto))
        ).andDo(print());

        //then
        perform
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("data[0].code").value("NotNull"))
        ;
    }

    @Test
    @DisplayName("회원가입 시 아무런 사항도 기입하지 않은 경우")
    public void signUp_Empty() throws Exception {
        //given
        MemberSocialSignUpRequestDto memberSocialSignUpRequestDto = MemberSocialSignUpRequestDto.builder().build();

        //when
        ResultActions perform = mockMvc.perform(
                post("/api/auth/social/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(memberSocialSignUpRequestDto))
        ).andDo(print());

        //then
        perform
                .andExpect(status().isBadRequest())
                .andDo(document(
                        "social-sign-up-empty",
                        requestHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("[application/json;charset=UTF-8] 로 지정")
                        ),
                        requestFields(
                                fieldWithPath("email").description("회원 식별에 필요한 Email 기입"),
                                fieldWithPath("nickname").description("회원 활동 시 필요한 Nickname 기입"),
                                fieldWithPath("socialType").description("Social 인증을 통해 회원가입을 진행하는 경우 어떠한 Social 매체를 사용하여 회원가입을 진행하는 지 명시"),
                                fieldWithPath("socialId").description("Social 인증을 통해 회원가입을 진행하는 경우 해당 Social 매체 내에서 회원을 식별하기 위해 사용하는 ID 값을 기입 + \n" +
                                        "해당 SocialId 를 통해 회원을 식별합니다.")
                        ),
                        responseFields(
                                fieldWithPath("status").description(STATUS_DESCRIPTION),
                                fieldWithPath("data[0].objectName").description("Validation Error 가 발생한 객체의 이름"),
                                fieldWithPath("data[0].code").description("발생한 Error 에 대한 Code"),
                                fieldWithPath("data[0].defaultMessage").description("발생한 Error 에 대한 Message"),
                                fieldWithPath("data[0].field").description("특정 Field 에서 Validation Error 가 발생한 경우 어떤 Field 에서 Error 가 발생했는지 명시").optional(),
                                fieldWithPath("message").description(MESSAGE_DESCRIPTION)
                        )
                ))
        ;

    }

    @Test
    @DisplayName("Refresh 성공")
    public void refresh_Success() throws Exception {
        //given
        MemberSocialSignUpRequestDto memberSocialSignUpRequestDto = createMemberSocialSignUpRequestDto();
        Long signUpMemberId = authenticationService.signUp(memberSocialSignUpRequestDto.toServiceDto());

        TokenDto tokenDto = authenticationService.signIn(signUpMemberId);
        OnlyRefreshTokenRequestDto onlyRefreshTokenRequestDto = new OnlyRefreshTokenRequestDto(tokenDto.getRefreshToken());

        //when
        ResultActions perform = mockMvc.perform(
                post("/api/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(onlyRefreshTokenRequestDto))
        ).andDo(print());

        //then
        perform
                .andExpect(status().isOk())
                .andExpect(jsonPath("status").value(REFRESH_SIGN_IN_SUCCESS.getStatus()))
                .andExpect(jsonPath("data").exists())
                .andExpect(jsonPath("message").value(REFRESH_SIGN_IN_SUCCESS.getMessage()))
                .andDo(document(
                        "refresh-sign-in-success",
                        requestHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("[application/json;charset=UTF-8] 지정")
                        ),
                        requestFields(
                                fieldWithPath("refreshToken").description("Access Token 을 재발급 받기 위해 필요한 Refresh Token 기입. +\n " +
                                        "소셜 로그인(소셜 회원가입 포함) 시점에 Access Token 과 함께 발급되는 Refresh Token")
                        ),
                        responseFields(
                                fieldWithPath("status").description(STATUS_DESCRIPTION),
                                fieldWithPath("data.memberId").description("인증된 회원의 식별 ID"),
                                fieldWithPath("data.accessToken.header").description("재발급된 Access Token 의 Header"),
                                fieldWithPath("data.accessToken.payload").description("재발급된 Access Token 의 Payload"),
                                fieldWithPath("data.accessToken.signature").description("재발급된 Access Token 의 Signature"),
                                fieldWithPath("data.accessTokenValidSecond").description("재발급된 Access Token 의 만료까지 남은 시간(초)"),
                                fieldWithPath("data.refreshToken").description("사용된 Refresh Token 다시 응답(Refresh Token 재발급을 위해서는 로그인을 다시 해야합니다.)"),
                                fieldWithPath("data.refreshTokenExpiredDate").description("사용된 Refresh Token 의 만료 날짜"),
                                fieldWithPath("message").description(MESSAGE_DESCRIPTION)
                        )
                ))
        ;

    }

    @Test
    @DisplayName("Refresh 토큰을 통한 회원 조회 실패")
    public void refresh_NotFound() throws Exception {
        //given

        OnlyRefreshTokenRequestDto onlyRefreshTokenRequestDto = new OnlyRefreshTokenRequestDto("fjewiofndsklfnldska");

        //when
        ResultActions perform = mockMvc.perform(
                post("/api/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(onlyRefreshTokenRequestDto))
        ).andDo(print());

        //then
        perform
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("status").value(REFRESH_TOKEN_NOT_FOUND.getStatus()))
                .andExpect(jsonPath("data").doesNotExist())
                .andExpect(jsonPath("message").value(REFRESH_TOKEN_NOT_FOUND.getMessage()))
        ;

    }

    @Transactional
    @Test
    @DisplayName("Refresh 토큰의 유효 기간이 만료된 경우")
    public void refresh_Expired(@Mock MemberSignUpDto memberSignUpDto) throws Exception {
        //given
        RefreshInfo refreshInfo = RefreshInfo.builder()
                .refreshToken(UUID.randomUUID().toString())
                .refreshTokenExpiredDate(LocalDateTime.now().minusDays(1))
                .build();

        Member member = Member.builder()
                .email("test@email.com")
                .nickname("test")
                .password("test")
                .refreshInfo(refreshInfo)
                .roles(Set.of(MemberRole.USER))
                .build();

        memberRepository.save(member);

        OnlyRefreshTokenRequestDto onlyRefreshTokenRequestDto = new OnlyRefreshTokenRequestDto(refreshInfo.getRefreshToken());

        //when
        ResultActions perform = mockMvc.perform(
                post("/api/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(onlyRefreshTokenRequestDto))
        ).andDo(print());

        //then
        perform
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("status").value(REFRESH_TOKEN_EXPIRED.getStatus()))
                .andExpect(jsonPath("data").doesNotExist())
                .andExpect(jsonPath("message").value(REFRESH_TOKEN_EXPIRED.getMessage()));

    }

    @Test
    @DisplayName("회원 탈퇴")
    public void withdrawal() throws Exception {
        //given
        MemberSocialSignUpRequestDto memberSocialSignUpRequestDto = createMemberSocialSignUpRequestDto();
        Long signUpId = authenticationService.signUp(memberSocialSignUpRequestDto.toServiceDto());

        TokenDto tokenDto = authenticationService.signIn(signUpId);

        //when
        ResultActions perform = mockMvc.perform(
                delete("/api/auth/" + signUpId + "/withdrawal")
                        .header(securityJwtProperties.getJwtTokenHeader(), tokenDto.getAccessToken())
        ).andDo(print());

        //then
        perform
                .andExpect(status().isOk())
                .andExpect(jsonPath("status").value(WITHDRAWAL_SUCCESS.getStatus()))
                .andExpect(jsonPath("data").doesNotExist())
                .andExpect(jsonPath("message").value(WITHDRAWAL_SUCCESS.getMessage()))
                .andDo(document(
                        "withdrawal-success",
                        requestHeaders(
                                headerWithName(securityJwtProperties.getJwtTokenHeader()).description(JWT_TOKEN_HEADER_DESCRIPTION)
                        ),
                        responseFields(
                                fieldWithPath("status").description(STATUS_DESCRIPTION),
                                fieldWithPath("data").description("[null]"),
                                fieldWithPath("message").description(MESSAGE_DESCRIPTION)
                        )
                ))
        ;

    }

    @Test
    @DisplayName("회원 탈퇴 - 탈퇴한 회원이 리소스 접근")
    public void withdrawal_By_WithdrawalMember() throws Exception {
         //given
        MemberSocialSignUpRequestDto memberSocialSignUpRequestDto = createMemberSocialSignUpRequestDto();
        Long signUpId = authenticationService.signUp(memberSocialSignUpRequestDto.toServiceDto());

        TokenDto tokenDto = authenticationService.signIn(signUpId);

        authenticationService.withdrawal(signUpId);

        //when
        ResultActions perform = mockMvc.perform(
                delete("/api/auth/" + signUpId + "/withdrawal")
                        .header(securityJwtProperties.getJwtTokenHeader(), tokenDto.getAccessToken())
        ).andDo(print());

        //then
        perform
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("status").value(FORBIDDEN.getStatus()))
                .andExpect(jsonPath("data").doesNotExist())
                .andExpect(jsonPath("message").value(FORBIDDEN.getMessage()));

    }

    @Test
    @DisplayName("회원 탈퇴 - 인증되지 않음")
    public void withdrawal_Unauthorized() throws Exception {
         //given
        MemberSocialSignUpRequestDto memberSocialSignUpRequestDto = createMemberSocialSignUpRequestDto();
        Long signUpId = authenticationService.signUp(memberSocialSignUpRequestDto.toServiceDto());

        TokenDto tokenDto = authenticationService.signIn(signUpId);

        //when
        ResultActions perform = mockMvc.perform(
                delete("/api/auth/" + signUpId + "/withdrawal")
//                        .header(securityJwtProperties.getJwtTokenHeader(), tokenDto.getAccessToken())
        ).andDo(print());


        //then
        perform
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("status").value(UNAUTHORIZED.getStatus()))
                .andExpect(jsonPath("data").doesNotExist())
                .andExpect(jsonPath("message").value(UNAUTHORIZED.getMessage()));

    }

    @Test
    @DisplayName("회원 탈퇴 - 다른 회원의 계정 탈퇴")
    public void withdrawal_DifferentMember() throws Exception {
         //given
        MemberSocialSignUpRequestDto memberSocialSignUpRequestDto = createMemberSocialSignUpRequestDto();
        Long signUpId = authenticationService.signUp(memberSocialSignUpRequestDto.toServiceDto());

        TokenDto tokenDto = authenticationService.signIn(signUpId);

        //when
        ResultActions perform = mockMvc.perform(
                delete("/api/auth/" + 100000L + "/withdrawal")
                        .header(securityJwtProperties.getJwtTokenHeader(), tokenDto.getAccessToken())
        ).andDo(print());


        //then
        perform
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("status").value(WITHDRAWAL_DIFFERENT_MEMBER.getStatus()))
                .andExpect(jsonPath("data").doesNotExist())
                .andExpect(jsonPath("message").value(WITHDRAWAL_DIFFERENT_MEMBER.getMessage()));

    }

    @Test
    @DisplayName("로그아웃")
    public void signOut() throws Exception {
        //given
        MemberSocialSignUpRequestDto memberSocialSignUpRequestDto = createMemberSocialSignUpRequestDto();
        Long signUpId = authenticationService.signUp(memberSocialSignUpRequestDto.toServiceDto());

        TokenDto tokenDto = authenticationService.signIn(signUpId);

        //when
        ResultActions perform = mockMvc.perform(
                get("/api/auth/" + signUpId + "/sign-out")
                        .header(securityJwtProperties.getJwtTokenHeader(), tokenDto.getAccessToken())
        ).andDo(print());

        //then
        perform
                .andExpect(status().isOk())
                .andExpect(jsonPath("status").value(SIGN_OUT_SUCCESS.getStatus()))
                .andExpect(jsonPath("data").doesNotExist())
                .andExpect(jsonPath("message").value(SIGN_OUT_SUCCESS.getMessage()))
                .andDo(document(
                        "sign-out-success",
                        requestHeaders(
                                headerWithName(securityJwtProperties.getJwtTokenHeader()).description(JWT_TOKEN_HEADER_DESCRIPTION)
                        ),
                        responseFields(
                                fieldWithPath("status").description(STATUS_DESCRIPTION),
                                fieldWithPath("data").description("[null]"),
                                fieldWithPath("message").description(MESSAGE_DESCRIPTION)
                        )
                ))
        ;

        //given
        String refreshToken = tokenDto.getRefreshToken();
        OnlyRefreshTokenRequestDto onlyRefreshTokenRequestDto = new OnlyRefreshTokenRequestDto(refreshToken);

        ResultActions perform2 = mockMvc.perform(
                post("/api/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(onlyRefreshTokenRequestDto))
        ).andDo(print());

        perform2
                .andExpect(status().isUnauthorized());

    }

    @Test
    @DisplayName("로그아웃 - 인증되지 않은 회원이 로그아웃")
    public void signOut_Unauthorized() throws Exception {
         //given
        MemberSocialSignUpRequestDto memberSocialSignUpRequestDto = createMemberSocialSignUpRequestDto();
        Long signUpId = authenticationService.signUp(memberSocialSignUpRequestDto.toServiceDto());

        TokenDto tokenDto = authenticationService.signIn(signUpId);

        //when
        ResultActions perform = mockMvc.perform(
                get("/api/auth/" + signUpId + "/sign-out")
//                        .header(securityJwtProperties.getJwtTokenHeader(), tokenDto.getAccessToken())
        ).andDo(print());

        //then
        perform
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("status").value(UNAUTHORIZED.getStatus()))
                .andExpect(jsonPath("data").doesNotExist())
                .andExpect(jsonPath("message").value(UNAUTHORIZED.getMessage()))
        ;

    }

    @Test
    @DisplayName("로그아웃 - 탈퇴한 회원이 로그아웃")
    public void signOut_WithdrawalMember() throws Exception {
         //given
        MemberSocialSignUpRequestDto memberSocialSignUpRequestDto = createMemberSocialSignUpRequestDto();
        Long signUpId = authenticationService.signUp(memberSocialSignUpRequestDto.toServiceDto());

        TokenDto tokenDto = authenticationService.signIn(signUpId);

        authenticationService.withdrawal(signUpId);

        //when
        ResultActions perform = mockMvc.perform(
                get("/api/auth/" + signUpId + "/sign-out")
                        .header(securityJwtProperties.getJwtTokenHeader(), tokenDto.getAccessToken())
        ).andDo(print());


        //then
        perform
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("status").value(FORBIDDEN.getStatus()))
                .andExpect(jsonPath("data").doesNotExist())
                .andExpect(jsonPath("message").value(FORBIDDEN.getMessage()));

    }

    @Test
    @DisplayName("로그아웃 - 다른 회원을 로그아웃")
    public void signOut_DifferentMember() throws Exception {
         //given
        MemberSocialSignUpRequestDto memberSocialSignUpRequestDto = createMemberSocialSignUpRequestDto();
        Long signUpId = authenticationService.signUp(memberSocialSignUpRequestDto.toServiceDto());

        TokenDto tokenDto = authenticationService.signIn(signUpId);

        //when
        ResultActions perform = mockMvc.perform(
                get("/api/auth/" + 10000L + "/sign-out")
                        .header(securityJwtProperties.getJwtTokenHeader(), tokenDto.getAccessToken())
        ).andDo(print());

        //then
        perform
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("status").value(SIGN_OUT_DIFFERENT_MEMBER.getStatus()))
                .andExpect(jsonPath("data").doesNotExist())
                .andExpect(jsonPath("message").value(SIGN_OUT_DIFFERENT_MEMBER.getMessage()));

    }

}