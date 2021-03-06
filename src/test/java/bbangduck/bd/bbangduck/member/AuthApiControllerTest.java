package bbangduck.bd.bbangduck.member;

import bbangduck.bd.bbangduck.domain.auth.dto.controller.MemberSocialSignUpRequestDto;
import bbangduck.bd.bbangduck.domain.auth.dto.controller.OnlyRefreshTokenRequestDto;
import bbangduck.bd.bbangduck.domain.auth.dto.service.MemberSignUpDto;
import bbangduck.bd.bbangduck.domain.auth.dto.service.TokenDto;
import bbangduck.bd.bbangduck.domain.auth.service.KakaoSignInService;
import bbangduck.bd.bbangduck.domain.member.dto.controller.request.CheckIfEmailIsAvailableRequestDto;
import bbangduck.bd.bbangduck.domain.member.dto.controller.request.MemberCheckIfNicknameIsAvailableRequestDto;
import bbangduck.bd.bbangduck.domain.member.entity.Member;
import bbangduck.bd.bbangduck.domain.member.entity.enbeded.RefreshInfo;
import bbangduck.bd.bbangduck.domain.member.enumerate.MemberRole;
import bbangduck.bd.bbangduck.domain.member.enumerate.SocialType;
import bbangduck.bd.bbangduck.domain.member.exception.MemberEmailDuplicateException;
import bbangduck.bd.bbangduck.domain.member.exception.MemberNicknameDuplicateException;
import bbangduck.bd.bbangduck.domain.member.exception.MemberSocialInfoDuplicateException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static bbangduck.bd.bbangduck.api.document.utils.DocUrl.MEMBER_ROOM_ESCAPE_RECODES_OPEN_STATUS;
import static bbangduck.bd.bbangduck.api.document.utils.DocUrl.SOCIAL_TYPE;
import static bbangduck.bd.bbangduck.api.document.utils.DocumentLinkGenerator.generateLinkCode;
import static bbangduck.bd.bbangduck.global.common.ResponseStatus.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("?????? ?????? API Controller ?????????")
@ExtendWith(MockitoExtension.class)
class AuthApiControllerTest extends BaseJGMApiControllerTest {

    @MockBean
    KakaoSignInService kakaoSignInService;

    @Test
    @DisplayName("?????? ???????????? ?????????")
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
        List<String> socialTypeList = Stream.of(SocialType.values()).map(Enum::name).collect(Collectors.toList());
        perform
                .andExpect(status().isCreated())
                .andDo(document(
                        "social-sign-up-success",
                        requestHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("[application/json;charset=UTF-8] ??? ??????")
                        ),
                        requestFields(
                                fieldWithPath("email").description("?????? ????????? ????????? Email ??????"),
                                fieldWithPath("nickname").description("?????? ?????? ??? ????????? Nickname ??????"),
                                fieldWithPath("socialType").description("Social ????????? ?????? ??????????????? ???????????? ?????? ????????? Social ????????? ???????????? ??????????????? ???????????? ??? ?????? + \n" +
                                        generateLinkCode(SOCIAL_TYPE)),
                                fieldWithPath("socialId").description("Social ????????? ?????? ??????????????? ???????????? ?????? ?????? Social ?????? ????????? ????????? ???????????? ?????? ???????????? ID ?????? ?????? + \n" +
                                        "?????? SocialId ??? ?????? ????????? ???????????????.")
                        ),
                        responseFields(
                                fieldWithPath("memberInfo.memberId").description("????????? ????????? ?????? ID"),
                                fieldWithPath("memberInfo.email").description("????????? ????????? Email"),
                                fieldWithPath("memberInfo.nickname").description("????????? ????????? Nickname"),
                                fieldWithPath("memberInfo.profileImage").description("????????? ????????? ????????? ????????? (null)"),
                                fieldWithPath("memberInfo.socialAccounts[0].socialId").description("Social ????????? ?????? ????????? ????????? Social ID"),
                                fieldWithPath("memberInfo.socialAccounts[0].socialType").description("Social ????????? ?????? ????????? ????????? ????????? Social ??????"),
                                fieldWithPath("memberInfo.description").description("????????? ?????? ?????? (null)"),
                                fieldWithPath("memberInfo.roomEscapeRecodesOpenStatus").description("????????? ?????? ?????? ?????? +\n" +
                                        generateLinkCode(MEMBER_ROOM_ESCAPE_RECODES_OPEN_STATUS)),
                                fieldWithPath("memberInfo.roomEscapeStatus.challengesCount").description("????????? ?????? ?????? ?????? [0]"),
                                fieldWithPath("memberInfo.roomEscapeStatus.successCount").description("????????? ?????? ???????????? ????????? ?????? [0]"),
                                fieldWithPath("memberInfo.roomEscapeStatus.failCount").description("????????? ?????? ???????????? ????????? ?????? [0]"),
                                fieldWithPath("memberInfo.playInclinations").description("????????? ????????? ?????? [null]"),
                                fieldWithPath("memberInfo.registerTimes").description("?????? ??????"),
                                fieldWithPath("memberInfo.updateTimes").description("?????? ?????? ?????? ?????? ??????"),
                                fieldWithPath("memberInfo.myProfile").description("????????? ???????????? ??????????????? ??????"),


                                fieldWithPath("tokenInfo.memberId").description("????????? ????????? ?????? ID"),
                                fieldWithPath("tokenInfo.accessToken.header").description("????????? ???????????? ????????? ?????? JWT ????????? Header"),
                                fieldWithPath("tokenInfo.accessToken.payload").description("????????? ???????????? ????????? ?????? JWT ????????? Payload"),
                                fieldWithPath("tokenInfo.accessToken.signature").description("????????? ???????????? ????????? ?????? JWT ????????? Signature"),
                                fieldWithPath("tokenInfo.accessTokenValidSecond").description("????????? ???????????? ????????? ?????? JWT ????????? ?????????????????? ?????? ??????"),
                                fieldWithPath("tokenInfo.refreshToken").description("???????????? ????????? ????????? ????????? Refresh ??????"),
                                fieldWithPath("tokenInfo.refreshTokenExpiredDate").description("???????????? ????????? ????????? ????????? Refresh ????????? ?????? ??????")
                        )
                ))
        ;

    }

    @Test
    @DisplayName("???????????? ????????? ?????? ?????????")
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
    @DisplayName("???????????? ????????? ?????? ?????????")
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
    @DisplayName("?????? ???????????? ??? ?????? ?????? ?????? ?????????")
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
    @DisplayName("?????? ???????????? ??? ???????????? ???????????? ?????? ??????")
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
    @DisplayName("?????? ???????????? ??? ???????????? ???????????? ?????? ??????")
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
    @DisplayName("?????? ???????????? ??? ?????? ID ??? ???????????? ?????? ??????")
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
    @DisplayName("?????? ???????????? ??? ?????? ????????? ???????????? ?????? ??????")
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
    @DisplayName("???????????? ??? ????????? ????????? ???????????? ?????? ??????")
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
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("[application/json;charset=UTF-8] ??? ??????")
                        ),
                        requestFields(
                                fieldWithPath("email").description("?????? ????????? ????????? Email ??????"),
                                fieldWithPath("nickname").description("?????? ?????? ??? ????????? Nickname ??????"),
                                fieldWithPath("socialType").description("Social ????????? ?????? ??????????????? ???????????? ?????? ????????? Social ????????? ???????????? ??????????????? ???????????? ??? ??????"),
                                fieldWithPath("socialId").description("Social ????????? ?????? ??????????????? ???????????? ?????? ?????? Social ?????? ????????? ????????? ???????????? ?????? ???????????? ID ?????? ?????? + \n" +
                                        "?????? SocialId ??? ?????? ????????? ???????????????.")
                        ),
                        responseFields(
                                fieldWithPath("status").description(STATUS_DESCRIPTION),
                                fieldWithPath("data[0].objectName").description("Validation Error ??? ????????? ????????? ??????"),
                                fieldWithPath("data[0].code").description("????????? Error ??? ?????? Code"),
                                fieldWithPath("data[0].defaultMessage").description("????????? Error ??? ?????? Message"),
                                fieldWithPath("data[0].field").description("?????? Field ?????? Validation Error ??? ????????? ?????? ?????? Field ?????? Error ??? ??????????????? ??????").optional(),
                                fieldWithPath("message").description(MESSAGE_DESCRIPTION)
                        )
                ))
        ;

    }

    @Test
    @DisplayName("Refresh ??????")
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
                .andDo(document(
                        "refresh-sign-in-success",
                        requestHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("[application/json;charset=UTF-8] ??????")
                        ),
                        requestFields(
                                fieldWithPath("refreshToken").description("Access Token ??? ????????? ?????? ?????? ????????? Refresh Token ??????. +\n " +
                                        "?????? ?????????(?????? ???????????? ??????) ????????? Access Token ??? ?????? ???????????? Refresh Token")
                        ),
                        responseFields(
                                fieldWithPath("memberId").description("????????? ????????? ?????? ID"),
                                fieldWithPath("accessToken.header").description("???????????? Access Token ??? Header"),
                                fieldWithPath("accessToken.payload").description("???????????? Access Token ??? Payload"),
                                fieldWithPath("accessToken.signature").description("???????????? Access Token ??? Signature"),
                                fieldWithPath("accessTokenValidSecond").description("???????????? Access Token ??? ???????????? ?????? ??????(???)"),
                                fieldWithPath("refreshToken").description("????????? Refresh Token ?????? ??????(Refresh Token ???????????? ???????????? ???????????? ?????? ???????????????.)"),
                                fieldWithPath("refreshTokenExpiredDate").description("????????? Refresh Token ??? ?????? ??????")
                        )
                ))
        ;

    }

    @Test
    @DisplayName("Refresh ????????? ?????? ?????? ?????? ??????")
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
    @DisplayName("Refresh ????????? ?????? ????????? ????????? ??????")
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
    @DisplayName("?????? ??????")
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
                .andExpect(status().isNoContent())
                .andDo(document(
                        "withdrawal-success",
                        requestHeaders(
                                headerWithName(securityJwtProperties.getJwtTokenHeader()).description(JWT_TOKEN_HEADER_DESCRIPTION)
                        )
                ))
        ;

    }

    @Test
    @DisplayName("?????? ?????? - ????????? ????????? ????????? ??????")
    public void withdrawal_By_WithdrawalMember() throws Exception {
         //given
        MemberSocialSignUpRequestDto memberSocialSignUpRequestDto = createMemberSocialSignUpRequestDto();
        Long signUpId = authenticationService.signUp(memberSocialSignUpRequestDto.toServiceDto());

        TokenDto tokenDto = authenticationService.signIn(signUpId);

        authenticationApplicationService.withdrawal(signUpId, signUpId);

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
    @DisplayName("?????? ?????? - ???????????? ??????")
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
    @DisplayName("?????? ?????? - ?????? ????????? ?????? ??????")
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
                .andExpect(jsonPath("status").value(MANIPULATE_OTHER_MEMBERS_INFO.getStatus()))
                .andExpect(jsonPath("data").doesNotExist())
                .andExpect(jsonPath("message").value(MANIPULATE_OTHER_MEMBERS_INFO.getMessage()));

    }

    @Test
    @DisplayName("????????????")
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
                .andExpect(status().isNoContent())
                .andDo(document(
                        "sign-out-success",
                        requestHeaders(
                                headerWithName(securityJwtProperties.getJwtTokenHeader()).description(JWT_TOKEN_HEADER_DESCRIPTION)
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
    @DisplayName("???????????? - ???????????? ?????? ????????? ????????????")
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
    @DisplayName("???????????? - ????????? ????????? ????????????")
    public void signOut_WithdrawalMember() throws Exception {
         //given
        MemberSocialSignUpRequestDto memberSocialSignUpRequestDto = createMemberSocialSignUpRequestDto();
        Long signUpId = authenticationService.signUp(memberSocialSignUpRequestDto.toServiceDto());

        TokenDto tokenDto = authenticationService.signIn(signUpId);

        authenticationApplicationService.withdrawal(signUpId, signUpId);

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
    @DisplayName("???????????? - ?????? ????????? ????????????")
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

    @Test
    @DisplayName("?????????, ????????? ?????? ?????? ??????")
    public void checkIfEmailAndNicknameIsAvailable() throws Exception {
        //given
        MemberSocialSignUpRequestDto memberSocialSignUpRequestDto = createMemberSocialSignUpRequestDto();
        authenticationService.signUp(memberSocialSignUpRequestDto.toServiceDto());

        CheckIfEmailIsAvailableRequestDto checkIfEmailIsAvailableRequestDto = new CheckIfEmailIsAvailableRequestDto(memberSocialSignUpRequestDto.getEmail());
        MemberCheckIfNicknameIsAvailableRequestDto memberCheckIfNicknameIsAvailableRequestDto = new MemberCheckIfNicknameIsAvailableRequestDto(memberSocialSignUpRequestDto.getNickname());

        //when
        ResultActions perform1 = mockMvc.perform(
                post("/api/auth/emails/check-availabilities")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(checkIfEmailIsAvailableRequestDto))
        ).andDo(print());

        ResultActions perform2 = mockMvc.perform(
                post("/api/auth/nicknames/check-availabilities")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(memberCheckIfNicknameIsAvailableRequestDto))
        ).andDo(print());

        //then
        perform1
                .andExpect(status().isOk())
                .andExpect(jsonPath("isAvailable").value(false))
                .andDo(document(
                        "check-if-email-is-available-success",
                        requestHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("[application/json;charset=UTF-8] ??????")
                        ),
                        requestFields(
                                fieldWithPath("email").description("?????? ?????? ????????? ?????? Email ??????")
                        ),
                        responseFields(
                                fieldWithPath("isAvailable").description("Email ?????? ?????? ?????? ?????? \n" +
                                        "true -> ?????? ??????, false -> ?????? ?????????")
                        )
                ))
        ;

        perform2
                .andExpect(status().isOk())
                .andExpect(jsonPath("isAvailable").value(false))
                .andDo(document(
                        "check-if-nickname-is-available-success",
                        requestHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("[application/json;charset=UTF-8] ??????")
                        ),
                        requestFields(
                                fieldWithPath("nickname").description("?????? ?????? ????????? ?????? Nickname ??????")
                        ),
                        responseFields(
                                fieldWithPath("isAvailable").description("Nickname ?????? ?????? ?????? ?????? +\n" +
                                        "true -> ?????? ??????, false -> ?????? ?????????")
                        )
                ))
        ;

        checkIfEmailIsAvailableRequestDto.setEmail("member");
        mockMvc.perform(
                post("/api/auth/emails/check-availabilities")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(checkIfEmailIsAvailableRequestDto))
        )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("status").value(CHECK_IF_EMAIL_IS_AVAILABLE_NOT_VALID.getStatus()))
                .andExpect(jsonPath("message").value(CHECK_IF_EMAIL_IS_AVAILABLE_NOT_VALID.getMessage()))
        ;


    }

}