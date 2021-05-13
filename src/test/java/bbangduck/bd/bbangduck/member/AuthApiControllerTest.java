package bbangduck.bd.bbangduck.member;

import bbangduck.bd.bbangduck.domain.auth.dto.OnlyRefreshTokenDto;
import bbangduck.bd.bbangduck.domain.auth.dto.TokenDto;
import bbangduck.bd.bbangduck.domain.member.dto.MemberSignUpDto;
import bbangduck.bd.bbangduck.domain.member.entity.SocialType;
import bbangduck.bd.bbangduck.global.common.ResponseStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class AuthApiControllerTest extends BaseJGMApiControllerTest {

    // TODO: 2021-05-06 회원가입 문서화 진행
    @Test
    @DisplayName("소셜 회원가입 테스트")
    public void signUpTest() throws Exception {
        //given
        MemberSignUpDto memberSignUpDto = MemberSignUpDto.builder()
                .email("test@email.com")
                .nickname("testNickname")
                .password("")
                .socialType(SocialType.KAKAO)
                .socialId("3123213")
                .build();


        //when
        ResultActions perform = mockMvc.perform(
                post("/api/auth/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(memberSignUpDto))
        ).andDo(print());

        //then
        perform
                .andExpect(status().isCreated())
                .andDo(document(
                        "sign-up-success",
                        requestHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("[application/json;charset=UTF-8] 로 지정")
                        ),
                        requestFields(
                                fieldWithPath("email").description("회원 식별에 필요한 Email 기입"),
                                fieldWithPath("nickname").description("회원 활동 시 필요한 Nickname 기입"),
                                fieldWithPath("password").description("로그인 시점에 필요한 비밀번호 기입 + \n" +
                                        "Social 인증을 통해 회원가입을 하는 경우 별도의 비밀번호를 기입하지 않습니다."),
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
                                fieldWithPath("data.memberInfo.registerDate").description("가입 날짜"),
                                fieldWithPath("data.memberInfo.updateDate").description("회원 정보 최종 수정 날짜"),
                                fieldWithPath("data.tokenDto.memberId").description("가입된 회원의 식별 ID"),
                                fieldWithPath("data.tokenDto.accessToken.header").description("가입된 회원에게 발급된 인증 JWT 토큰의 Header"),
                                fieldWithPath("data.tokenDto.accessToken.payload").description("가입된 회원에게 발급된 인증 JWT 토큰의 Payload"),
                                fieldWithPath("data.tokenDto.accessToken.signature").description("가입된 회원에게 발급된 인증 JWT 토큰의 Signature"),
                                fieldWithPath("data.tokenDto.accessTokenValidSecond").description("가입된 회원에게 발급된 인증 JWT 토큰이 만료되기까지 남은 시간"),
                                fieldWithPath("data.tokenDto.refreshToken").description("인증토큰 재발급 요청에 필요한 Refresh 토큰"),
                                fieldWithPath("data.tokenDto.refreshTokenExpiredDate").description("인증토큰 재발급 요청에 필요한 Refresh 토큰의 만료 날짜"),
                                fieldWithPath("message").description(MESSAGE_DESCRIPTION)
                        )
                ))
        ;

    }

    @Test
    @DisplayName("회원가입 이메일 중복 테스트")
    public void signUp_EmailDuplicate() throws Exception {
        //given
        MemberSignUpDto memberSignUpDto = MemberSignUpDto.builder()
                .email("test@email.com")
                .nickname("testNickname")
                .password("")
                .socialType(SocialType.KAKAO)
                .socialId("3123213")
                .build();

        authenticationService.signUp(memberSignUpDto.signUp(REFRESH_TOKEN_EXPIRED_DATE));


        MemberSignUpDto memberSignUpDto2 = MemberSignUpDto.builder()
                .email("test@email.com")
                .nickname("testNickname2")
                .password("")
                .socialType(SocialType.KAKAO)
                .socialId("31233131213")
                .build();


        //when
        ResultActions perform = mockMvc.perform(
                post("/api/auth/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(memberSignUpDto2))
        ).andDo(print());

        //then
        perform
                .andExpect(status().isConflict())
                .andExpect(jsonPath("message").value(ResponseStatus.MEMBER_EMAIL_DUPLICATE.getMessage()))
                .andExpect(jsonPath("data").doesNotExist())
                .andExpect(jsonPath("status").value(ResponseStatus.MEMBER_EMAIL_DUPLICATE.getStatus()))
        ;

    }

    @Test
    @DisplayName("회원가입 닉네임 중복 테스트")
    public void signUp_NicknameDuplicate() throws Exception {
        //given
        MemberSignUpDto memberSignUpDto = MemberSignUpDto.builder()
                .email("test@email.com")
                .nickname("testNickname")
                .password("")
                .socialType(SocialType.KAKAO)
                .socialId("3123213")
                .build();

        authenticationService.signUp(memberSignUpDto.signUp(REFRESH_TOKEN_EXPIRED_DATE));


        MemberSignUpDto memberSignUpDto2 = MemberSignUpDto.builder()
                .email("test@email.com2")
                .nickname("testNickname")
                .password("")
                .socialType(SocialType.KAKAO)
                .socialId("312331312213")
                .build();

        //when
        ResultActions perform = mockMvc.perform(
                post("/api/auth/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(memberSignUpDto2))
        ).andDo(print());

        //then
        perform
                .andExpect(status().isConflict())
                .andExpect(jsonPath("message").value(ResponseStatus.MEMBER_NICKNAME_DUPLICATE.getMessage()))
                .andExpect(jsonPath("data").doesNotExist())
                .andExpect(jsonPath("status").value(ResponseStatus.MEMBER_NICKNAME_DUPLICATE.getStatus()))
        ;

    }

    @Test
    @DisplayName("소셜 회원가입 시 소셜 정보 중복 테스트")
    public void signUp_SocialInfoDuplicate() throws Exception {
        //given
        MemberSignUpDto memberSignUpDto = MemberSignUpDto.builder()
                .email("test@email.com")
                .nickname("testNickname")
                .password("")
                .socialType(SocialType.KAKAO)
                .socialId("3123213")
                .build();

        authenticationService.signUp(memberSignUpDto.signUp(REFRESH_TOKEN_EXPIRED_DATE));


        MemberSignUpDto memberSignUpDto2 = MemberSignUpDto.builder()
                .email("test2@email.com")
                .nickname("testNickname2")
                .password("")
                .socialType(SocialType.KAKAO)
                .socialId("3123213")
                .build();

        //when
        ResultActions perform = mockMvc.perform(
                post("/api/auth/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(memberSignUpDto2))
        ).andDo(print());

        //then
        perform
                .andExpect(status().isConflict())
                .andExpect(jsonPath("status").value(ResponseStatus.MEMBER_SOCIAL_INFO_DUPLICATE.getStatus()))
                .andExpect(jsonPath("data").doesNotExist())
                .andExpect(jsonPath("message").value(ResponseStatus.MEMBER_SOCIAL_INFO_DUPLICATE.getMessage()))
        ;

    }

    @Test
    @DisplayName("소셜 회원가입 시 이메일을 기입하지 않은 경우")
    public void signUpTest_EmailEmpty() throws Exception {
        //given
        MemberSignUpDto memberSignUpDto = MemberSignUpDto.builder()
                .email("")
                .nickname("testNickname")
                .password("")
                .socialType(SocialType.KAKAO)
                .socialId("3123213")
                .build();


        //when
        ResultActions perform = mockMvc.perform(
                post("/api/auth/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(memberSignUpDto))
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
        MemberSignUpDto memberSignUpDto = MemberSignUpDto.builder()
                .email("test@email.com")
                .nickname("")
                .password("")
                .socialType(SocialType.KAKAO)
                .socialId("321321321")
                .build();


        //when
        ResultActions perform = mockMvc.perform(
                post("/api/auth/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(memberSignUpDto))
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
        MemberSignUpDto memberSignUpDto = MemberSignUpDto.builder()
                .email("test@email.com")
                .nickname("test")
                .password("")
                .socialType(SocialType.KAKAO)
                .socialId("")
                .build();


        //when
        ResultActions perform = mockMvc.perform(
                post("/api/auth/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(memberSignUpDto))
        ).andDo(print());

        //then
        perform
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("data[0].code").value("WrongSocialInfo"))
        ;
    }

    @Test
    @DisplayName("소셜 회원가입 시 소셜 타입을 기입하지 않은 경우")
    public void signUpTest_SocialTypeEmpty() throws Exception {
        //given
        MemberSignUpDto memberSignUpDto = MemberSignUpDto.builder()
                .email("test@email.com")
                .nickname("test")
                .password("")
                .socialType(null)
                .socialId("321321321")
                .build();


        //when
        ResultActions perform = mockMvc.perform(
                post("/api/auth/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(memberSignUpDto))
        ).andDo(print());

        //then
        perform
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("data[0].code").value("WrongSocialInfo"))
        ;
    }

    @Test
    @DisplayName("일반 회원가입 시 비밀번호 기입하지 않은 경우")
    public void signUpTest_PasswordEmpty() throws Exception {
        //given
        MemberSignUpDto memberSignUpDto = MemberSignUpDto.builder()
                .email("test@email.com")
                .nickname("test")
                .password(null)
                .socialType(null)
                .socialId(null)
                .build();


        //when
        ResultActions perform = mockMvc.perform(
                post("/api/auth/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(memberSignUpDto))
        ).andDo(print());

        //then
        perform
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("data[0].code").value("BlankPassword"))
        ;
    }

    @Test
    @DisplayName("소셜 회원가입 시 비밀번호를 기입한 경우")
    public void signUpTest_NoPasswordRequired() throws Exception {
        //given
        MemberSignUpDto memberSignUpDto = MemberSignUpDto.builder()
                .email("test@email.com")
                .nickname("testNickname")
                .password("test")
                .socialType(SocialType.KAKAO)
                .socialId("3123213")
                .build();


        //when
        ResultActions perform = mockMvc.perform(
                post("/api/auth/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(memberSignUpDto))
        ).andDo(print());

        //then
        perform
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("data[0].code").value("NoPasswordRequired"))
        ;
    }

    @Test
    @DisplayName("회원가입 시 아무런 사항도 기입하지 않은 경우")
    public void signUp_Empty() throws Exception {
        //given
        MemberSignUpDto memberSignUpDto = MemberSignUpDto.builder().build();

        //when
        ResultActions perform = mockMvc.perform(
                post("/api/auth/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(memberSignUpDto))
        ).andDo(print());

        //then
        perform
                .andExpect(status().isBadRequest())
                .andDo(document(
                        "sign-up-empty",
                        requestHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("[application/json;charset=UTF-8] 로 지정")
                        ),
                        requestFields(
                                fieldWithPath("email").description("회원 식별에 필요한 Email 기입"),
                                fieldWithPath("nickname").description("회원 활동 시 필요한 Nickname 기입"),
                                fieldWithPath("password").description("로그인 시점에 필요한 비밀번호 기입 + \n" +
                                        "Social 인증을 통해 회원가입을 하는 경우 별도의 비밀번호를 기입하지 않습니다."),
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
        MemberSignUpDto memberSignUpDto = createMemberSignUpDto();
        Long signUpMemberId = authenticationService.signUp(memberSignUpDto.signUp(securityJwtProperties.getRefreshTokenExpiredDate()));

        TokenDto tokenDto = authenticationService.signIn(signUpMemberId);
        OnlyRefreshTokenDto onlyRefreshTokenDto = new OnlyRefreshTokenDto(tokenDto.getRefreshToken());

        //when
        ResultActions perform = mockMvc.perform(
                post("/api/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(onlyRefreshTokenDto))
        ).andDo(print());

        //then
        perform
                .andExpect(status().isOk())
                .andExpect(jsonPath("status").value(ResponseStatus.REFRESH_SIGN_IN_SUCCESS.getStatus()))
                .andExpect(jsonPath("data").exists())
                .andExpect(jsonPath("message").value(ResponseStatus.REFRESH_SIGN_IN_SUCCESS.getMessage()))
        ;
    }

    @Test
    @DisplayName("Refresh 토큰을 통한 회원 조회 실패")
    public void refresh_NotFound() {
        //given

        //when

        //then

    }

    @Test
    @DisplayName("Refresh 토큰의 유효 기간이 만료된 경우")
    public void refresh_Expired() {
        //given

        //when

        //then

    }

}