package bbangduck.bd.bbangduck.member;

import bbangduck.bd.bbangduck.domain.member.dto.MemberSignUpDto;
import bbangduck.bd.bbangduck.domain.member.entity.SocialType;
import bbangduck.bd.bbangduck.global.common.ResponseStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class AuthApiControllerTest extends BaseJGMApiControllerTest {

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
                .andExpect(status().isCreated());
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

}