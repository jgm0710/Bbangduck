package bbangduck.bd.bbangduck.domain.member.controller;

import bbangduck.bd.bbangduck.domain.auth.dto.controller.MemberSocialSignUpRequestDto;
import bbangduck.bd.bbangduck.domain.auth.dto.service.TokenDto;
import bbangduck.bd.bbangduck.domain.auth.service.KakaoSignInService;
import bbangduck.bd.bbangduck.domain.file.entity.FileStorage;
import bbangduck.bd.bbangduck.domain.member.dto.controller.request.*;
import bbangduck.bd.bbangduck.domain.member.dto.service.MemberProfileImageDto;
import bbangduck.bd.bbangduck.domain.member.enumerate.MemberRoomEscapeRecodesOpenStatus;
import bbangduck.bd.bbangduck.domain.member.enumerate.MemberSearchKeywordType;
import bbangduck.bd.bbangduck.domain.member.enumerate.SocialType;
import bbangduck.bd.bbangduck.domain.member.exception.FoundMemberIsWithdrawalOrBanException;
import bbangduck.bd.bbangduck.domain.member.exception.MemberNicknameDuplicateException;
import bbangduck.bd.bbangduck.domain.member.exception.MemberProfileImageNotFoundException;
import bbangduck.bd.bbangduck.domain.review.dto.controller.request.ReviewCreateRequestDto;
import bbangduck.bd.bbangduck.domain.theme.entity.Theme;
import bbangduck.bd.bbangduck.global.common.ResponseStatus;
import bbangduck.bd.bbangduck.member.BaseJGMApiControllerTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.ResultActions;

import static bbangduck.bd.bbangduck.api.document.utils.DocUrl.*;
import static bbangduck.bd.bbangduck.api.document.utils.DocumentLinkGenerator.generateLinkCode;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.JsonFieldType.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("??????(????????? ??????) ?????? API Controller ?????????")
class MemberApiControllerTest extends BaseJGMApiControllerTest {

    @MockBean
    KakaoSignInService kakaoSignInService;

    @Test
    @DisplayName("?????? ????????? ????????? ??????")
    public void updateProfileImage() throws Exception {
        //given
        MockMultipartFile files = createMockMultipartFile("files", IMAGE_FILE_CLASS_PATH);
        Long uploadImageFileId = fileStorageService.uploadImageFile(files);
        FileStorage storedFile = fileStorageService.getStoredFile(uploadImageFileId);

        MemberSocialSignUpRequestDto memberSocialSignUpRequestDto = createMemberSocialSignUpRequestDto();
        Long signUpId = authenticationService.signUp(memberSocialSignUpRequestDto.toServiceDto());
        TokenDto tokenDto = authenticationService.signIn(signUpId);

        MemberUpdateProfileImageRequestDto memberUpdateProfileImageRequestDto = new MemberUpdateProfileImageRequestDto(storedFile.getId(), storedFile.getFileName());

        //when
        ResultActions perform = mockMvc.perform(
                put("/api/members/" + signUpId + "/profiles/images")
                        .header(securityJwtProperties.getJwtTokenHeader(), tokenDto.getAccessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(memberUpdateProfileImageRequestDto))
        ).andDo(print());

        //then
        perform
                .andExpect(status().isNoContent())
                .andDo(document(
                        "update-profile-image-success",
                        requestHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("[application/json;charset=UTF-8] ??????"),
                                headerWithName(securityJwtProperties.getJwtTokenHeader()).description(JWT_TOKEN_HEADER_DESCRIPTION)
                        ),
                        requestFields(
                                fieldWithPath("fileStorageId").description("????????? ????????? ????????? ?????? ????????? ID"),
                                fieldWithPath("fileName").description("????????? ????????? ????????? ??????")
                        )
                ))
        ;

    }

    @Test
    @DisplayName("???????????? ?????? ????????? ????????? ???????????? ???????????? ??????")
    public void updateProfileImage_Unauthorized() throws Exception {
        //given
        MockMultipartFile files = createMockMultipartFile("files", IMAGE_FILE_CLASS_PATH);
        Long uploadImageFileId = fileStorageService.uploadImageFile(files);
        FileStorage storedFile = fileStorageService.getStoredFile(uploadImageFileId);

        MemberSocialSignUpRequestDto memberSocialSignUpRequestDto = createMemberSocialSignUpRequestDto();
        Long signUpId = authenticationService.signUp(memberSocialSignUpRequestDto.toServiceDto());
        TokenDto tokenDto = authenticationService.signIn(signUpId);

        MemberUpdateProfileImageRequestDto memberUpdateProfileImageRequestDto = new MemberUpdateProfileImageRequestDto(storedFile.getId(), storedFile.getFileName());

        //when
        ResultActions perform = mockMvc.perform(
                put("/api/members/" + signUpId + "/profiles/images")
//                        .header(securityJwtProperties.getJwtTokenHeader(), tokenDto.getAccessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(memberUpdateProfileImageRequestDto))
        ).andDo(print());

        //then
        perform
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("status").value(ResponseStatus.UNAUTHORIZED.getStatus()))
                .andExpect(jsonPath("data").doesNotExist())
                .andExpect(jsonPath("message").value(ResponseStatus.UNAUTHORIZED.getMessage()))
        ;

    }

    @Test
    @DisplayName("?????? ????????? ????????? ?????? ??? ?????? ????????? ????????? ???????????? ???????????? ??????")
    public void updateProfileImage_UpdateDifferentMember() throws Exception {
        //given
        MockMultipartFile files = createMockMultipartFile("files", IMAGE_FILE_CLASS_PATH);
        Long uploadImageFileId = fileStorageService.uploadImageFile(files);
        FileStorage storedFile = fileStorageService.getStoredFile(uploadImageFileId);

        MemberSocialSignUpRequestDto memberSocialSignUpRequestDto = createMemberSocialSignUpRequestDto();
        Long signUpId = authenticationService.signUp(memberSocialSignUpRequestDto.toServiceDto());
        TokenDto tokenDto = authenticationService.signIn(signUpId);

        MemberUpdateProfileImageRequestDto memberUpdateProfileImageRequestDto = new MemberUpdateProfileImageRequestDto(storedFile.getId(), storedFile.getFileName());

        memberSocialSignUpRequestDto.setEmail("test2@email.com");
        memberSocialSignUpRequestDto.setNickname("test2");
        memberSocialSignUpRequestDto.setSocialId("312312312222");
        Long signUp2Id = authenticationService.signUp(memberSocialSignUpRequestDto.toServiceDto());

        //when
        ResultActions perform = mockMvc.perform(
                put("/api/members/" + signUp2Id + "/profiles/images")
                        .header(securityJwtProperties.getJwtTokenHeader(), tokenDto.getAccessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(memberUpdateProfileImageRequestDto))
        ).andDo(print());

        //then
        perform
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("status").value(ResponseStatus.UPDATE_DIFFERENT_MEMBER_PROFILE.getStatus()))
                .andExpect(jsonPath("data").doesNotExist())
                .andExpect(jsonPath("message").value(ResponseStatus.UPDATE_DIFFERENT_MEMBER_PROFILE.getMessage()));

    }

    @Test
    @DisplayName("?????? ????????? ????????? ?????? - ????????? ????????? ???????????? ????????? ??????")
    public void updateProfileImage_ExistProfileImage() throws Exception {
        //given
        MockMultipartFile files = createMockMultipartFile("files", IMAGE_FILE_CLASS_PATH);
        Long uploadImageFileId = fileStorageService.uploadImageFile(files);
        FileStorage storedFile = fileStorageService.getStoredFile(uploadImageFileId);

        MemberSocialSignUpRequestDto memberSocialSignUpRequestDto = createMemberSocialSignUpRequestDto();
        Long signUpId = authenticationService.signUp(memberSocialSignUpRequestDto.toServiceDto());
        TokenDto tokenDto = authenticationService.signIn(signUpId);

        MockMultipartFile files2 = createMockMultipartFile("files", IMAGE_FILE2_CLASS_PATH);
        Long uploadId = fileStorageService.uploadImageFile(files2);
        FileStorage storedFile2 = fileStorageService.getStoredFile(uploadId);
        MemberProfileImageDto memberProfileImageDto2 = new MemberProfileImageDto(storedFile2.getId(), storedFile2.getFileName());
        memberService.updateProfileImage(signUpId, memberProfileImageDto2);

        MemberUpdateProfileImageRequestDto memberUpdateProfileImageRequestDto = new MemberUpdateProfileImageRequestDto(storedFile.getId(), storedFile.getFileName());

        //when
        ResultActions perform = mockMvc.perform(
                put("/api/members/" + signUpId + "/profiles/images")
                        .header(securityJwtProperties.getJwtTokenHeader(), tokenDto.getAccessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(memberUpdateProfileImageRequestDto))
        ).andDo(print());

        //then
        perform
                .andExpect(status().isNoContent())
        ;

    }

    @Test
    @DisplayName("?????? ????????? ????????? ?????? - ?????? ????????? ???????????? ?????? ??????")
    public void updateProfileImage_FileInfoEmpty() throws Exception {
        //given
        MockMultipartFile files = createMockMultipartFile("files", IMAGE_FILE_CLASS_PATH);
        Long uploadImageFileId = fileStorageService.uploadImageFile(files);
        FileStorage storedFile = fileStorageService.getStoredFile(uploadImageFileId);

        MemberSocialSignUpRequestDto memberSocialSignUpRequestDto = createMemberSocialSignUpRequestDto();
        Long signUpId = authenticationService.signUp(memberSocialSignUpRequestDto.toServiceDto());
        TokenDto tokenDto = authenticationService.signIn(signUpId);

        MemberUpdateProfileImageRequestDto memberUpdateProfileImageRequestDto = new MemberUpdateProfileImageRequestDto(null, "");

        //when
        ResultActions perform = mockMvc.perform(
                put("/api/members/" + signUpId + "/profiles/images")
                        .header(securityJwtProperties.getJwtTokenHeader(), tokenDto.getAccessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(memberUpdateProfileImageRequestDto))
        ).andDo(print());

        //then
        perform
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("status").value(ResponseStatus.MEMBER_UPDATE_PROFILE_IMAGE_NOT_VALID.getStatus()))
                .andExpect(jsonPath("data[0]").exists())
                .andExpect(jsonPath("message").value(ResponseStatus.MEMBER_UPDATE_PROFILE_IMAGE_NOT_VALID.getMessage()))
                .andDo(document(
                        "update-profile-image-file-info-empty",
                        requestHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("[application/json;charset=UTF-8] ??????"),
                                headerWithName(securityJwtProperties.getJwtTokenHeader()).description(JWT_TOKEN_HEADER_DESCRIPTION)
                        ),
                        requestFields(
                                fieldWithPath("fileStorageId").description("????????? ????????? ????????? ?????? ????????? ID"),
                                fieldWithPath("fileName").description("????????? ????????? ????????? ??????")
                        ),
                        responseFields(
                                fieldWithPath("status").description(STATUS_DESCRIPTION),
                                fieldWithPath("data[0].objectName").description("????????? ????????? ?????? ????????? ??????"),
                                fieldWithPath("data[0].code").description("?????? ??????"),
                                fieldWithPath("data[0].defaultMessage").description("????????? ????????? ?????? ?????????"),
                                fieldWithPath("data[0].field").description("????????? ????????? ?????? ????????? ??????"),
                                fieldWithPath("message").description(MESSAGE_DESCRIPTION)
                        )
                ))
        ;

    }


    @Test
    @DisplayName("?????? ????????? ????????? ??????")
    public void deleteProfileImage() throws Exception {
        //given
        MemberSocialSignUpRequestDto memberSocialSignUpRequestDto = createMemberSocialSignUpRequestDto();
        Long signUpId = authenticationService.signUp(memberSocialSignUpRequestDto.toServiceDto());

        MockMultipartFile files = createMockMultipartFile("files", IMAGE_FILE_CLASS_PATH);
        Long uploadId = fileStorageService.uploadImageFile(files);
        FileStorage storedFile = fileStorageService.getStoredFile(uploadId);
        MemberProfileImageDto memberProfileImageDto = new MemberProfileImageDto(storedFile.getId(), storedFile.getFileName());

        memberService.updateProfileImage(signUpId, memberProfileImageDto);

        TokenDto tokenDto = authenticationService.signIn(signUpId);

        //when
        ResultActions perform = mockMvc.perform(
                delete("/api/members/" + signUpId + "/profiles/images")
                        .header(securityJwtProperties.getJwtTokenHeader(), tokenDto.getAccessToken())
        ).andDo(print());

        //then
        perform
                .andExpect(status().isNoContent())
                .andDo(document(
                        "delete-profile-image-success",
                        requestHeaders(
                                headerWithName(securityJwtProperties.getJwtTokenHeader()).description(JWT_TOKEN_HEADER_DESCRIPTION)
                        )
                ))
        ;

    }

    @Test
    @DisplayName("?????? ????????? ????????? ?????? - ?????? ????????? ????????? ????????? ??????")
    public void deleteProfileImage_DifferentMember() throws Exception {
        //given
        MemberSocialSignUpRequestDto memberSocialSignUpRequestDto = createMemberSocialSignUpRequestDto();
        Long signUpId = authenticationService.signUp(memberSocialSignUpRequestDto.toServiceDto());

        MockMultipartFile files = createMockMultipartFile("files", IMAGE_FILE_CLASS_PATH);
        Long uploadId = fileStorageService.uploadImageFile(files);
        FileStorage storedFile = fileStorageService.getStoredFile(uploadId);
        MemberProfileImageDto memberProfileImageDto = new MemberProfileImageDto(storedFile.getId(), storedFile.getFileName());

        memberService.updateProfileImage(signUpId, memberProfileImageDto);

        TokenDto tokenDto = authenticationService.signIn(signUpId);

        //when
        ResultActions perform = mockMvc.perform(
                delete("/api/members/" + 10000L + "/profiles/images")
                        .header(securityJwtProperties.getJwtTokenHeader(), tokenDto.getAccessToken())
        ).andDo(print());


        //then
        perform
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("status").value(ResponseStatus.UPDATE_DIFFERENT_MEMBER_PROFILE.getStatus()))
                .andExpect(jsonPath("data").doesNotExist())
                .andExpect(jsonPath("message").value(ResponseStatus.UPDATE_DIFFERENT_MEMBER_PROFILE.getMessage()));

    }

    @Test
    @DisplayName("?????? ????????? ????????? ?????? - ???????????? ?????? ??????")
    public void deleteProfileImage_Unauthorized() throws Exception {
        //given
        MemberSocialSignUpRequestDto memberSocialSignUpRequestDto = createMemberSocialSignUpRequestDto();
        Long signUpId = authenticationService.signUp(memberSocialSignUpRequestDto.toServiceDto());

        MockMultipartFile files = createMockMultipartFile("files", IMAGE_FILE_CLASS_PATH);
        Long uploadId = fileStorageService.uploadImageFile(files);
        FileStorage storedFile = fileStorageService.getStoredFile(uploadId);
        MemberProfileImageDto memberProfileImageDto = new MemberProfileImageDto(storedFile.getId(), storedFile.getFileName());

        memberService.updateProfileImage(signUpId, memberProfileImageDto);

        TokenDto tokenDto = authenticationService.signIn(signUpId);

        //when
        ResultActions perform = mockMvc.perform(
                delete("/api/members/" + signUpId + "/profiles/images")
//                        .header(securityJwtProperties.getJwtTokenHeader(), tokenDto.getAccessToken())
        ).andDo(print());

        //then
        perform
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("status").value(ResponseStatus.UNAUTHORIZED.getStatus()))
                .andExpect(jsonPath("data").doesNotExist())
                .andExpect(jsonPath("message").value(ResponseStatus.UNAUTHORIZED.getMessage()));

    }

    @Test
    @DisplayName("?????? ????????? ????????? ?????? - ?????? ????????? ????????? ???????????? ?????? ?????? ??????")
    public void deleteProfileImage_ProfileImageNotExist() throws Exception {
        //given
        MemberSocialSignUpRequestDto memberSocialSignUpRequestDto = createMemberSocialSignUpRequestDto();
        Long signUpId = authenticationService.signUp(memberSocialSignUpRequestDto.toServiceDto());

        TokenDto tokenDto = authenticationService.signIn(signUpId);

        //when
        ResultActions perform = mockMvc.perform(
                delete("/api/members/" + signUpId + "/profiles/images")
                        .header(securityJwtProperties.getJwtTokenHeader(), tokenDto.getAccessToken())
        ).andDo(print());


        //then
        MemberProfileImageNotFoundException memberProfileImageNotFoundException = new MemberProfileImageNotFoundException();
        perform
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("status").value(memberProfileImageNotFoundException.getStatus()))
                .andExpect(jsonPath("data").doesNotExist())
                .andExpect(jsonPath("message").value(memberProfileImageNotFoundException.getMessage()));

    }


    @Test
    @DisplayName("?????? ????????? ??????")
    public void updateNicknameTest() throws Exception {
        //given
        MemberSocialSignUpRequestDto memberSocialSignUpRequestDto = createMemberSocialSignUpRequestDto();
        Long signUpId = authenticationService.signUp(memberSocialSignUpRequestDto.toServiceDto());

        String newNickname = "newUpdateNickname";
        MemberUpdateNicknameRequestDto memberUpdateNicknameRequestDto = new MemberUpdateNicknameRequestDto(newNickname);

        TokenDto tokenDto = authenticationService.signIn(signUpId);

        //when
        ResultActions perform = mockMvc.perform(
                put("/api/members/" + signUpId + "/nicknames")
                        .header(securityJwtProperties.getJwtTokenHeader(), tokenDto.getAccessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(memberUpdateNicknameRequestDto))
        ).andDo(print());

        //then
        perform
                .andExpect(status().isNoContent())
                .andDo(document(
                        "update-nickname-success",
                        requestHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("[application/json;charset=UTF-8] ??????"),
                                headerWithName(securityJwtProperties.getJwtTokenHeader()).description(JWT_TOKEN_HEADER_DESCRIPTION)
                        ),
                        requestFields(
                                fieldWithPath("nickname").description("????????? Nickname")
                        )
                ))
        ;

    }

    @Test
    @DisplayName("?????? ????????? ?????? - ???????????? ?????? ??????")
    public void updateNickname_Unauthorized() throws Exception {
        //given
        MemberSocialSignUpRequestDto memberSocialSignUpRequestDto = createMemberSocialSignUpRequestDto();
        Long signUpId = authenticationService.signUp(memberSocialSignUpRequestDto.toServiceDto());

        String newNickname = "newUpdateNickname";
        MemberUpdateNicknameRequestDto memberUpdateNicknameRequestDto = new MemberUpdateNicknameRequestDto(newNickname);

        TokenDto tokenDto = authenticationService.signIn(signUpId);

        //when
        ResultActions perform = mockMvc.perform(
                put("/api/members/" + signUpId + "/nicknames")
//                        .header(securityJwtProperties.getJwtTokenHeader(), tokenDto.getAccessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(memberUpdateNicknameRequestDto))
        ).andDo(print());

        //then
        perform
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("status").value(ResponseStatus.UNAUTHORIZED.getStatus()))
                .andExpect(jsonPath("data").doesNotExist())
                .andExpect(jsonPath("message").value(ResponseStatus.UNAUTHORIZED.getMessage()));

    }


    @Test
    @DisplayName("?????? ????????? ?????? - ???????????? ???????????? ?????? ??????")
    public void updateNickname_NicknameEmpty() throws Exception {
        //given
        MemberSocialSignUpRequestDto memberSocialSignUpRequestDto = createMemberSocialSignUpRequestDto();
        Long signUpId = authenticationService.signUp(memberSocialSignUpRequestDto.toServiceDto());

        String newNickname = "";
        MemberUpdateNicknameRequestDto memberUpdateNicknameRequestDto = new MemberUpdateNicknameRequestDto(newNickname);
        TokenDto tokenDto = authenticationService.signIn(signUpId);

        //when
        ResultActions perform = mockMvc.perform(
                put("/api/members/" + signUpId + "/nicknames")
                        .header(securityJwtProperties.getJwtTokenHeader(), tokenDto.getAccessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(memberUpdateNicknameRequestDto))
        ).andDo(print());


        //then
        perform
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("status").value(ResponseStatus.MEMBER_UPDATE_NICKNAME_NOT_VALID.getStatus()))
                .andExpect(jsonPath("data").exists())
                .andExpect(jsonPath("message").value(ResponseStatus.MEMBER_UPDATE_NICKNAME_NOT_VALID.getMessage()))
                .andDo(document(
                        "update-nickname-empty",
                        requestHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("[application/json;charset=UTF-8] ??????"),
                                headerWithName(securityJwtProperties.getJwtTokenHeader()).description(JWT_TOKEN_HEADER_DESCRIPTION)
                        ),
                        requestFields(
                                fieldWithPath("nickname").description("????????? Nickname")
                        ),
                        responseFields(
                                fieldWithPath("status").description(STATUS_DESCRIPTION),
                                fieldWithPath("data[0].objectName").description("????????? ????????? ????????? ??????"),
                                fieldWithPath("data[0].code").description("?????? ??????"),
                                fieldWithPath("data[0].defaultMessage").description("????????? ????????? ?????? ?????????"),
                                fieldWithPath("data[0].field").description("????????? ????????? ????????? ??????"),
                                fieldWithPath("message").description(MESSAGE_DESCRIPTION)
                        )
                ))
        ;

    }

    @Test
    @DisplayName("?????? ????????? ?????? - ?????? ????????? ???????????? ???????????? ??????")
    public void updateNickname_DifferentMember() throws Exception {
        //given
        MemberSocialSignUpRequestDto memberSocialSignUpRequestDto = createMemberSocialSignUpRequestDto();
        Long signUpId = authenticationService.signUp(memberSocialSignUpRequestDto.toServiceDto());

        String newNickname = "newUpdateNickname";
        MemberUpdateNicknameRequestDto memberUpdateNicknameRequestDto = new MemberUpdateNicknameRequestDto(newNickname);
        TokenDto tokenDto = authenticationService.signIn(signUpId);

        //when
        ResultActions perform = mockMvc.perform(
                put("/api/members/" + 10000L + "/nicknames")
                        .header(securityJwtProperties.getJwtTokenHeader(), tokenDto.getAccessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(memberUpdateNicknameRequestDto))
        ).andDo(print());


        //then
        perform
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("status").value(ResponseStatus.UPDATE_DIFFERENT_MEMBER_PROFILE.getStatus()))
                .andExpect(jsonPath("data").doesNotExist())
                .andExpect(jsonPath("message").value(ResponseStatus.UPDATE_DIFFERENT_MEMBER_PROFILE.getMessage()));

    }

    @Test
    @DisplayName("?????? ????????? ?????? - ?????? ????????? Nickname ??? ???????????? ??????")
    public void updateNickname_NicknameDuplicate() throws Exception {
        //given
        MemberSocialSignUpRequestDto memberSocialSignUpRequestDto = createMemberSocialSignUpRequestDto();
        Long signUpId = authenticationService.signUp(memberSocialSignUpRequestDto.toServiceDto());

        String newNickname = "newUpdateNickname";

        memberSocialSignUpRequestDto.setEmail("test2@email.com");
        memberSocialSignUpRequestDto.setNickname(newNickname);
        memberSocialSignUpRequestDto.setSocialId("332211");
        authenticationService.signUp(memberSocialSignUpRequestDto.toServiceDto());

        MemberUpdateNicknameRequestDto memberUpdateNicknameRequestDto = new MemberUpdateNicknameRequestDto(newNickname);

        TokenDto tokenDto = authenticationService.signIn(signUpId);

        //when
        ResultActions perform = mockMvc.perform(
                put("/api/members/" + signUpId + "/nicknames")
                        .header(securityJwtProperties.getJwtTokenHeader(), tokenDto.getAccessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(memberUpdateNicknameRequestDto))
        ).andDo(print());


        //then
        perform
                .andExpect(status().isConflict())
                .andExpect(jsonPath("status").value(ResponseStatus.MEMBER_NICKNAME_DUPLICATE.getStatus()))
                .andExpect(jsonPath("data").doesNotExist())
                .andExpect(jsonPath("message").value(new MemberNicknameDuplicateException(newNickname).getMessage()));

    }


    @Test
    @DisplayName("?????? ???????????? ??????")
    public void updateDescriptionTest() throws Exception {
        //given
        MemberSocialSignUpRequestDto memberSocialSignUpRequestDto = createMemberSocialSignUpRequestDto();
        Long signUpId = authenticationService.signUp(memberSocialSignUpRequestDto.toServiceDto());

        memberService.updateDescription(signUpId, "??? ?????? ?????? ??????");

        TokenDto tokenDto = authenticationService.signIn(signUpId);

        MemberUpdateDescriptionRequestDto memberUpdateDescriptionRequestDto = new MemberUpdateDescriptionRequestDto("????????? ?????? ?????? ??????");

        //when
        ResultActions perform = mockMvc.perform(
                put("/api/members/" + signUpId + "/descriptions")
                        .header(securityJwtProperties.getJwtTokenHeader(), tokenDto.getAccessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(memberUpdateDescriptionRequestDto))
        ).andDo(print());

        //then
        perform
                .andExpect(status().isNoContent())
                .andDo(document(
                        "update-description-success",
                        requestHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("[application/json;charset=UTF-8] ??????"),
                                headerWithName(securityJwtProperties.getJwtTokenHeader()).description(JWT_TOKEN_HEADER_DESCRIPTION)
                        ),
                        requestFields(
                                fieldWithPath("description").description("????????? ???????????? ??????")
                        )
                ))
        ;

    }


    @Test
    @DisplayName("?????? ???????????? ?????? - ??????????????? ???????????? ?????? ??????")
    public void updateDescription_Empty() throws Exception {
        //given
        MemberSocialSignUpRequestDto memberSocialSignUpRequestDto = createMemberSocialSignUpRequestDto();
        Long signUpId = authenticationService.signUp(memberSocialSignUpRequestDto.toServiceDto());

        memberService.updateDescription(signUpId, "??? ?????? ?????? ??????");

        TokenDto tokenDto = authenticationService.signIn(signUpId);

        MemberUpdateDescriptionRequestDto memberUpdateDescriptionRequestDto = new MemberUpdateDescriptionRequestDto(null);

        //when
        ResultActions perform = mockMvc.perform(
                put("/api/members/" + signUpId + "/descriptions")
                        .header(securityJwtProperties.getJwtTokenHeader(), tokenDto.getAccessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(memberUpdateDescriptionRequestDto))
        ).andDo(print());

        //then
        perform
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("status").value(ResponseStatus.MEMBER_UPDATE_DESCRIPTION_NOT_VALID.getStatus()))
                .andExpect(jsonPath("data").exists())
                .andExpect(jsonPath("message").value(ResponseStatus.MEMBER_UPDATE_DESCRIPTION_NOT_VALID.getMessage()))
                .andDo(document(
                        "update-description-empty",
                        requestHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("[application/json;charset=UTF-8] ??????"),
                                headerWithName(securityJwtProperties.getJwtTokenHeader()).description(JWT_TOKEN_HEADER_DESCRIPTION)
                        ),
                        requestFields(
                                fieldWithPath("description").description("????????? ???????????? ??????")
                        ),
                        responseFields(
                                fieldWithPath("status").description(STATUS_DESCRIPTION),
                                fieldWithPath("data[0].objectName").description(OBJECT_NAME_DESCRIPTION),
                                fieldWithPath("data[0].code").description(CODE_DESCRIPTION),
                                fieldWithPath("data[0].defaultMessage").description(DEFAULT_MESSAGE_DESCRIPTION),
                                fieldWithPath("data[0].field").description(FIELD_DESCRIPTION),
                                fieldWithPath("message").description(MESSAGE_DESCRIPTION)
                        )
                ))
        ;

    }

    @Test
    @DisplayName("?????? ???????????? ?????? - ???????????? ?????? ??????")
    public void updateDescription_Unauthorized() throws Exception {
        //given
        MemberSocialSignUpRequestDto memberSocialSignUpRequestDto = createMemberSocialSignUpRequestDto();
        Long signUpId = authenticationService.signUp(memberSocialSignUpRequestDto.toServiceDto());

        memberService.updateDescription(signUpId, "??? ?????? ?????? ??????");

        TokenDto tokenDto = authenticationService.signIn(signUpId);

        MemberUpdateDescriptionRequestDto memberUpdateDescriptionRequestDto = new MemberUpdateDescriptionRequestDto("????????? ????????????");

        //when
        ResultActions perform = mockMvc.perform(
                put("/api/members/" + signUpId + "/descriptions")
//                        .header(securityJwtProperties.getJwtTokenHeader(), tokenDto.getAccessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(memberUpdateDescriptionRequestDto))
        ).andDo(print());


        //then
        perform
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("status").value(ResponseStatus.UNAUTHORIZED.getStatus()))
                .andExpect(jsonPath("data").doesNotExist())
                .andExpect(jsonPath("message").value(ResponseStatus.UNAUTHORIZED.getMessage()));

    }

    @Test
    @DisplayName("?????? ???????????? ?????? - ?????? ????????? ???????????? ??????")
    public void updateDescription_DifferentMember() throws Exception {
        //given
        MemberSocialSignUpRequestDto memberSocialSignUpRequestDto = createMemberSocialSignUpRequestDto();
        Long signUpId = authenticationService.signUp(memberSocialSignUpRequestDto.toServiceDto());

        memberService.updateDescription(signUpId, "??? ?????? ?????? ??????");

        TokenDto tokenDto = authenticationService.signIn(signUpId);

        MemberUpdateDescriptionRequestDto memberUpdateDescriptionRequestDto = new MemberUpdateDescriptionRequestDto("????????? ????????????");

        //when
        ResultActions perform = mockMvc.perform(
                put("/api/members/" + 10000L + "/descriptions")
                        .header(securityJwtProperties.getJwtTokenHeader(), tokenDto.getAccessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(memberUpdateDescriptionRequestDto))
        ).andDo(print());

        //then
        perform
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("status").value(ResponseStatus.UPDATE_DIFFERENT_MEMBER_PROFILE.getStatus()))
                .andExpect(jsonPath("data").doesNotExist())
                .andExpect(jsonPath("message").value(ResponseStatus.UPDATE_DIFFERENT_MEMBER_PROFILE.getMessage()));

    }

    @Test
    @DisplayName("?????? ????????? ?????? ?????? ?????? ??????")
    public void updateRoomEscapeRecodesOpenStatus() throws Exception {
        //given
        MemberSocialSignUpRequestDto memberSocialSignUpRequestDto = createMemberSocialSignUpRequestDto();
        Long signUpId = authenticationService.signUp(memberSocialSignUpRequestDto.toServiceDto());

        TokenDto tokenDto = authenticationService.signIn(signUpId);

        MemberRoomEscapeRecodesOpenStatusUpdateRequestDto memberRoomEscapeRecodesOpenStatusUpdateRequestDto = new MemberRoomEscapeRecodesOpenStatusUpdateRequestDto(MemberRoomEscapeRecodesOpenStatus.CLOSE);
        //when
        ResultActions perform = mockMvc.perform(
                put("/api/members/{memberId}/room-escape-recodes-open-status", signUpId)
                        .header(securityJwtProperties.getJwtTokenHeader(), tokenDto.getAccessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(memberRoomEscapeRecodesOpenStatusUpdateRequestDto))
        ).andDo(print());

        //then
        perform
                .andExpect(status().isNoContent())
                .andDo(document(
                        "update-room-escape-recodes-open-status-success",
                        requestHeaders(
                                headerWithName(securityJwtProperties.getJwtTokenHeader()).description(JWT_TOKEN_HEADER_DESCRIPTION)
                        ),
                        requestFields(
                                fieldWithPath("roomEscapeRecodesOpenStatus").description("????????? ????????? ?????? ?????? ?????? ?????? +\n" +
                                        generateLinkCode(MEMBER_ROOM_ESCAPE_RECODES_OPEN_STATUS))
                        )
                ))
        ;

    }

    @Test
    @DisplayName("?????? ????????? ?????? ?????? ?????? ?????? - ?????? ????????? ????????? ?????? ?????? ?????? ??????")
    public void updateRoomEscapeRecodesOpenStatus_DifferentMember() throws Exception {
        //given
        MemberSocialSignUpRequestDto memberSocialSignUpRequestDto = createMemberSocialSignUpRequestDto();
        Long signUpId = authenticationService.signUp(memberSocialSignUpRequestDto.toServiceDto());

        TokenDto tokenDto = authenticationService.signIn(signUpId);

        MemberRoomEscapeRecodesOpenStatusUpdateRequestDto memberRoomEscapeRecodesOpenStatusUpdateRequestDto = new MemberRoomEscapeRecodesOpenStatusUpdateRequestDto(MemberRoomEscapeRecodesOpenStatus.CLOSE);

        //when
        ResultActions perform = mockMvc.perform(
                put("/api/members/{memberId}/room-escape-recodes-open-status", 10000L)
                        .header(securityJwtProperties.getJwtTokenHeader(), tokenDto.getAccessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(memberRoomEscapeRecodesOpenStatusUpdateRequestDto))
        ).andDo(print());


        //then
        perform
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("status").value(ResponseStatus.UPDATE_DIFFERENT_MEMBER_PROFILE.getStatus()))
                .andExpect(jsonPath("data").doesNotExist())
                .andExpect(jsonPath("message").value(ResponseStatus.UPDATE_DIFFERENT_MEMBER_PROFILE.getMessage()));
    }

    @Test
    @DisplayName("?????? ????????? ?????? ?????? ?????? ?????? - ???????????? ????????? ??????")
    public void updateRoomEscapeRecodesOpenStatus_Unauthorized() throws Exception {
        //given
        MemberSocialSignUpRequestDto memberSocialSignUpRequestDto = createMemberSocialSignUpRequestDto();
        Long signUpId = authenticationService.signUp(memberSocialSignUpRequestDto.toServiceDto());

        TokenDto tokenDto = authenticationService.signIn(signUpId);

        MemberRoomEscapeRecodesOpenStatusUpdateRequestDto memberRoomEscapeRecodesOpenStatusUpdateRequestDto = new MemberRoomEscapeRecodesOpenStatusUpdateRequestDto(MemberRoomEscapeRecodesOpenStatus.CLOSE);
        //when
        ResultActions perform = mockMvc.perform(
                put("/api/members/{memberId}/room-escape-recodes-open-status", signUpId)
//                        .header(securityJwtProperties.getJwtTokenHeader(), tokenDto.getAccessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(memberRoomEscapeRecodesOpenStatusUpdateRequestDto))
        ).andDo(print());


        //then
        perform
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("status").value(ResponseStatus.UNAUTHORIZED.getStatus()))
                .andExpect(jsonPath("data").doesNotExist())
                .andExpect(jsonPath("message").value(ResponseStatus.UNAUTHORIZED.getMessage()));

    }

    @Test
    @DisplayName("?????? ????????? ?????? - ?????? ????????? ???????????? ???????????? ??????")
    public void getProfile_DifferentMember() throws Exception {
        //given
        MemberSocialSignUpRequestDto memberSocialSignUpRequestDto = createMemberSocialSignUpRequestDto();
        Long signUpId = authenticationService.signUp(memberSocialSignUpRequestDto.toServiceDto());

        MockMultipartFile files = createMockMultipartFile("files", IMAGE_FILE_CLASS_PATH);
        Long uploadImageFileId = fileStorageService.uploadImageFile(files);
        FileStorage storedFile = fileStorageService.getStoredFile(uploadImageFileId);

        memberService.updateProfileImage(signUpId, new MemberProfileImageDto(storedFile.getId(), storedFile.getFileName()));
        memberService.updateDescription(signUpId, "test description");

        Theme themeSample = createThemeSample();

        ReviewCreateRequestDto reviewCreateRequestDto = createReviewCreateRequestDto(null);
        reviewApplicationService.createReview(signUpId, themeSample.getId(), reviewCreateRequestDto.toServiceDto());

        memberSocialSignUpRequestDto.setEmail("member2@email.com");
        memberSocialSignUpRequestDto.setNickname("member2");
        memberSocialSignUpRequestDto.setSocialId("27103271897");
        Long member2Id = authenticationService.signUp(memberSocialSignUpRequestDto.toServiceDto());

        TokenDto tokenDto = authenticationService.signIn(member2Id);

        //when
        ResultActions perform = mockMvc.perform(
                get("/api/members/{memberId}/profiles", signUpId)
                        .header(securityJwtProperties.getJwtTokenHeader(), tokenDto.getAccessToken())
        ).andDo(print());

        //then
        perform
                .andExpect(status().isOk())
                .andDo(document(
                        "get-different-member-profile-success",
                        requestHeaders(
                                headerWithName(securityJwtProperties.getJwtTokenHeader()).description(JWT_TOKEN_HEADER_DESCRIPTION)
                        ),
                        responseFields(
                                fieldWithPath("memberId").type(NUMBER).description("????????? ????????? ?????? ID"),
                                fieldWithPath("profileImage.profileImageId").type(NUMBER).description("????????? ???????????? ????????? ????????? ???????????? ?????? ID \n" +
                                        "????????? ????????? ?????? ?????? ?????? ????????? ??? ??????"),
                                fieldWithPath("profileImage.profileImageUrl").type(STRING).description("????????? ???????????? ????????? ????????? ????????? ???????????? URL"),
                                fieldWithPath("profileImage.profileImageThumbnailUrl").type(STRING).description("????????? ???????????? ????????? ????????? ???????????? ????????? ????????? ???????????? URL"),
                                fieldWithPath("nickname").type(STRING).description("????????? ????????? ?????????"),
                                fieldWithPath("description").type(STRING).description("????????? ????????? ????????????"),
                                fieldWithPath("roomEscapeStatus.challengesCount").type(NUMBER).description("????????? ????????? ????????? ????????? ??? ?????? +\n" +
                                        "(????????? ????????? ???????????? ???????????? ??????)"),
                                fieldWithPath("roomEscapeStatus.successCount").type(NUMBER).description("????????? ????????? ?????? ???????????? ????????? ??????"),
                                fieldWithPath("roomEscapeStatus.failCount").type(NUMBER).description("????????? ????????? ?????? ???????????? ????????? ??????"),
                                fieldWithPath("roomEscapeRecodesOpenStatus").type(STRING).description("????????? ????????? ????????? ?????? ?????? ?????? +\n" +
                                        generateLinkCode(MEMBER_ROOM_ESCAPE_RECODES_OPEN_STATUS)),
                                fieldWithPath("playInclinations").type(ARRAY).description("????????? ????????? ????????? ??????"),
                                fieldWithPath("playInclinations[].genre").type(STRING).description("????????? ????????? ????????? ????????? ????????? ?????? +\n" +
                                        generateLinkCode(GENRE)),
                                fieldWithPath("playInclinations[].playCount").type(NUMBER).description("????????? ????????? ????????? ????????? ????????? ????????? ???????????? ??????"),
                                fieldWithPath("myProfile").description("????????? ???????????? ??????????????? ??????")
                        )
                ))
        ;

    }

    @Test
    @DisplayName("?????? ????????? ?????? - ????????? ???????????? ???????????? ??????")
    public void getProfile_MyProfile() throws Exception {
        //given
        MemberSocialSignUpRequestDto memberSocialSignUpRequestDto = createMemberSocialSignUpRequestDto();
        Long signUpId = authenticationService.signUp(memberSocialSignUpRequestDto.toServiceDto());

        MockMultipartFile files = createMockMultipartFile("files", IMAGE_FILE_CLASS_PATH);
        Long uploadImageFileId = fileStorageService.uploadImageFile(files);
        FileStorage storedFile = fileStorageService.getStoredFile(uploadImageFileId);

        memberService.updateProfileImage(signUpId, new MemberProfileImageDto(storedFile.getId(), storedFile.getFileName()));
        memberService.updateDescription(signUpId, "test description");

        Theme themeSample = createThemeSample();

        ReviewCreateRequestDto reviewCreateRequestDto = createReviewCreateRequestDto(null);
        reviewApplicationService.createReview(signUpId, themeSample.getId(), reviewCreateRequestDto.toServiceDto());

        TokenDto tokenDto = authenticationService.signIn(signUpId);

        //when
        ResultActions perform = mockMvc.perform(
                get("/api/members/{memberId}/profiles", signUpId)
                        .header(securityJwtProperties.getJwtTokenHeader(), tokenDto.getAccessToken())
        ).andDo(print());

        //then
        perform
                .andExpect(status().isOk())
                .andDo(document(
                        "get-my-profile-success",
                        requestHeaders(
                                headerWithName(securityJwtProperties.getJwtTokenHeader()).description(JWT_TOKEN_HEADER_DESCRIPTION)
                        ),
                        responseFields(
                                fieldWithPath("memberId").description("????????? ????????? ?????? ID"),
                                fieldWithPath("profileImage.profileImageId").description("????????? ???????????? ????????? ????????? ???????????? ?????? ID \n" +
                                        "????????? ????????? ?????? ?????? ?????? ????????? ??? ??????"),
                                fieldWithPath("profileImage.profileImageUrl").description("????????? ???????????? ????????? ????????? ????????? ???????????? URL"),
                                fieldWithPath("profileImage.profileImageThumbnailUrl").description("????????? ???????????? ????????? ????????? ???????????? ????????? ????????? ???????????? URL"),
                                fieldWithPath("nickname").description("????????? ????????? ?????????"),
                                fieldWithPath("description").description("????????? ????????? ????????????"),
                                fieldWithPath("roomEscapeStatus.challengesCount").description("????????? ????????? ????????? ????????? ??? ?????? +\n" +
                                        "(????????? ????????? ???????????? ???????????? ??????)"),
                                fieldWithPath("roomEscapeStatus.successCount").description("????????? ????????? ?????? ???????????? ????????? ??????"),
                                fieldWithPath("roomEscapeStatus.failCount").description("????????? ????????? ?????? ???????????? ????????? ??????"),
                                fieldWithPath("roomEscapeRecodesOpenStatus").description("????????? ????????? ????????? ?????? ?????? ?????? +\n" +
                                        generateLinkCode(MEMBER_ROOM_ESCAPE_RECODES_OPEN_STATUS)),
                                fieldWithPath("playInclinations").description("????????? ????????? ????????? ??????"),
                                fieldWithPath("playInclinations[].genre").description("????????? ????????? ????????? ????????? ????????? ?????? +\n" +
                                        generateLinkCode(GENRE)),
                                fieldWithPath("playInclinations[].playCount").description("????????? ????????? ????????? ????????? ????????? ????????? ???????????? ??????"),
                                fieldWithPath("email").description("????????? ????????? Email"),
                                fieldWithPath("socialAccounts[0].socialId").description("????????? ????????? ????????? ?????? ????????? (????????? ??? ??? ????????????.) ?????? ID (?????? ???????????? ????????? ?????? ?????? ID)"),
                                fieldWithPath("socialAccounts[0].socialType").description("????????? ????????? ????????? ?????? ????????? (????????? ??? ??? ????????????.) ?????? (?????? ?????? ????????? ?????? ??????????????????)"),
                                fieldWithPath("registerTimes").description("????????? ????????? ?????? ?????? (????????????)"),
                                fieldWithPath("updateTimes").description("????????? ????????? ?????? ?????? (??????????????? ??????????????? ????????? ??????)"),
                                fieldWithPath("myProfile").description("????????? ???????????? ??????????????? ??????")
                        )
                ))
        ;

    }

    @Test
    @DisplayName("?????? ????????? ?????? - ????????? ????????? ?????? ????????? ???????????? ??????")
    public void getProfile_WithdrawalMember() throws Exception {
        //given
        MemberSocialSignUpRequestDto memberSocialSignUpRequestDto = createMemberSocialSignUpRequestDto();
        Long signUpId = authenticationService.signUp(memberSocialSignUpRequestDto.toServiceDto());

        MockMultipartFile files = createMockMultipartFile("files", IMAGE_FILE_CLASS_PATH);
        Long uploadImageFileId = fileStorageService.uploadImageFile(files);
        FileStorage storedFile = fileStorageService.getStoredFile(uploadImageFileId);

        memberService.updateProfileImage(signUpId, new MemberProfileImageDto(storedFile.getId(), storedFile.getFileName()));
        memberService.updateDescription(signUpId, "test description");

        Theme themeSample = createThemeSample();

        ReviewCreateRequestDto reviewCreateRequestDto = createReviewCreateRequestDto(null);
        reviewApplicationService.createReview(signUpId, themeSample.getId(), reviewCreateRequestDto.toServiceDto());

        memberSocialSignUpRequestDto.setEmail("member2@email.com");
        memberSocialSignUpRequestDto.setNickname("member2");
        memberSocialSignUpRequestDto.setSocialId("27103271897");
        Long member2Id = authenticationService.signUp(memberSocialSignUpRequestDto.toServiceDto());

        TokenDto tokenDto = authenticationService.signIn(member2Id);

        authenticationApplicationService.withdrawal(signUpId, signUpId);

        //when
        ResultActions perform = mockMvc.perform(
                get("/api/members/{memberId}/profiles", signUpId)
                        .header(securityJwtProperties.getJwtTokenHeader(), tokenDto.getAccessToken())
        ).andDo(print());

        //then
        perform
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("status").value(ResponseStatus.FOUND_MEMBER_IS_WITHDRAWAL_OR_BAN.getStatus()))
                .andExpect(jsonPath("data").doesNotExist())
                .andExpect(jsonPath("message").value(new FoundMemberIsWithdrawalOrBanException(signUpId).getMessage()));

    }

    @Test
    @DisplayName("???????????? ?????? ????????? ?????? ???????????? ???????????? ??????")
    public void getMemberProfile_Unauthorized() throws Exception {
        //given
        MemberSocialSignUpRequestDto memberSocialSignUpRequestDto = MemberSocialSignUpRequestDto.builder()
                .email("test@email.com")
                .nickname("testNickname")

                .socialType(SocialType.KAKAO)
                .socialId("3213123")
                .build();
        Long signUpMemberId = authenticationService.signUp(memberSocialSignUpRequestDto.toServiceDto());

        TokenDto tokenDto = authenticationService.signIn(signUpMemberId);
        String totalAccessToken = tokenDto.getAccessToken();

        //when
        ResultActions perform = mockMvc.perform(
                get("/api/members/{memberId}/profiles", signUpMemberId)
                        .header(securityJwtProperties.getJwtTokenHeader(), totalAccessToken + "fjdiajfinwkfndwkl")
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
    @DisplayName("?????? ?????? ??? ?????? ????????? ?????? ??? ?????? ??????")
    public void getMember_NotFound() throws Exception {
        //given
        MemberSocialSignUpRequestDto memberSocialSignUpRequestDto = MemberSocialSignUpRequestDto.builder()
                .email("test@email.com")
                .nickname("testNickname")

                .socialType(SocialType.KAKAO)
                .socialId("3213123")
                .build();
        Long signUpMemberId = authenticationService.signUp(memberSocialSignUpRequestDto.toServiceDto());

        TokenDto tokenDto = authenticationService.signIn(signUpMemberId);
        String totalAccessToken = tokenDto.getAccessToken();

        //when
        ResultActions perform = mockMvc.perform(
                get("/api/members/{memberId}/profiles", 10000L)
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
    @DisplayName("????????? ????????? ????????? ???????????? ???????????? ??????")
    public void getMember_By_WithdrawalMember() throws Exception {
        //given
        MemberSocialSignUpRequestDto memberSocialSignUpRequestDto = MemberSocialSignUpRequestDto.builder()
                .email("test@email.com")
                .nickname("testNickname")

                .socialType(SocialType.KAKAO)
                .socialId("3213123")
                .build();
        Long signUpMemberId = authenticationService.signUp(memberSocialSignUpRequestDto.toServiceDto());

        TokenDto tokenDto = authenticationService.signIn(signUpMemberId);
        String totalAccessToken = tokenDto.getAccessToken();

        authenticationApplicationService.withdrawal(signUpMemberId, signUpMemberId);

        //when
        ResultActions perform = mockMvc.perform(
                get("/api/members/{memberId}/profiles", signUpMemberId)
                        .header(securityJwtProperties.getJwtTokenHeader(), totalAccessToken)
        ).andDo(print());

        //then
        perform
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("status").value(ResponseStatus.FORBIDDEN.getStatus()))
                .andExpect(jsonPath("message").value(ResponseStatus.FORBIDDEN.getMessage()))
                .andExpect(jsonPath("data").doesNotExist());

    }

    @Test
    @DisplayName("????????? ????????? ?????? ??????")
    public void getMemberPlayInclinations() throws Exception {
        //given
        MemberSocialSignUpRequestDto memberSocialSignUpRequestDto = createMemberSocialSignUpRequestDto();
        Long signUpId = authenticationService.signUp(memberSocialSignUpRequestDto.toServiceDto());

        Theme themeSample = createThemeSample();

        ReviewCreateRequestDto reviewCreateRequestDto = createReviewCreateRequestDto(null);
        reviewApplicationService.createReview(signUpId, themeSample.getId(), reviewCreateRequestDto.toServiceDto());
        reviewApplicationService.createReview(signUpId, themeSample.getId(), reviewCreateRequestDto.toServiceDto());

        TokenDto tokenDto = authenticationService.signIn(signUpId);
        //when
        ResultActions perform = mockMvc.perform(
                get("/api/members/{memberId}/play-inclinations", signUpId)
                        .header(securityJwtProperties.getJwtTokenHeader(), tokenDto.getAccessToken())
        ).andDo(print());

        //then
        perform
                .andExpect(status().isOk())
                .andDo(document(
                        "get-member-play-inclination-success",
                        requestHeaders(
                                headerWithName(securityJwtProperties.getJwtTokenHeader()).description(JWT_TOKEN_HEADER_DESCRIPTION)
                        ),
                        responseFields(
                                fieldWithPath("playInclinations").type(ARRAY).description("????????? ????????? ??????"),
                                fieldWithPath("playInclinations[].genre").type(STRING).description("????????? ????????? ????????? ????????? ?????? +\n" +
                                        generateLinkCode(GENRE)),
                                fieldWithPath("playInclinations[].playCount").type(NUMBER).description("????????? ????????? ????????? ????????? ????????? ???????????? ??????"),
                                fieldWithPath("totalThemeEvaluatesCount").type(NUMBER).description("????????? ????????? ????????? ??? ??????")
                        )
                ))
        ;

    }

    @Test
    @DisplayName("?????? ?????? - ??????????????? ?????? ??????")
    public void searchMember_ByNickname() throws Exception {
        //given
        MemberSocialSignUpRequestDto memberSocialSignUpRequestDto = createMemberSocialSignUpRequestDto();
        Long memberId = authenticationService.signUp(memberSocialSignUpRequestDto.toServiceDto());

        MockMultipartFile files = createMockMultipartFile("files", IMAGE_FILE_CLASS_PATH);
        Long uploadFileId = fileStorageService.uploadImageFile(files);
        FileStorage storedFile = fileStorageService.getStoredFile(uploadFileId);

        MemberProfileImageDto memberProfileImageDto = MemberProfileImageDto.builder()
                .fileStorageId(storedFile.getId())
                .fileName(storedFile.getFileName())
                .build();

        memberService.updateProfileImage(memberId, memberProfileImageDto);
        memberService.updateDescription(memberId, "sample description");

        ReviewCreateRequestDto reviewCreateRequestDto = createReviewCreateRequestDto(null);
        Theme themeSample = createThemeSample();
        reviewApplicationService.createReview(memberId, themeSample.getId(), reviewCreateRequestDto.toServiceDto());

        MemberSearchRequestDto memberSearchRequestDto = MemberSearchRequestDto.builder()
                .searchType(MemberSearchKeywordType.NICKNAME)
                .keyword(memberSocialSignUpRequestDto.getNickname())
                .build();

        TokenDto tokenDto = authenticationService.signIn(memberId);

        //when
        ResultActions perform = mockMvc.perform(
                post("/api/members/search")
                        .header(securityJwtProperties.getJwtTokenHeader(), tokenDto.getAccessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(memberSearchRequestDto))
        ).andDo(print());

        //then
        perform
                .andExpect(status().isOk())
                .andDo(document(
                        "search-member-success",
                        requestHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("[application/json;charset=UTF-8] ??????"),
                                headerWithName(securityJwtProperties.getJwtTokenHeader()).description(JWT_TOKEN_HEADER_DESCRIPTION)
                        ),
                        requestFields(
                                fieldWithPath("searchType").description("?????? ?????? ??? ???????????? ?????? ????????? ?????????, ???????????? ?????? ????????? ????????? ?????? +\n" +
                                        generateLinkCode(MEMBER_SEARCH_KEYWORD_TYPE)),
                                fieldWithPath("keyword").description("?????? ?????? ??? ????????? ????????? +\n" +
                                        "????????? ?????????, ???????????? ???????????? ????????? ??? ????????????.")
                        ),
                        responseFields(
                                fieldWithPath("memberId").type(NUMBER).description("????????? ????????? ?????? ID"),
                                fieldWithPath("profileImage.profileImageId").type(NUMBER).description("????????? ????????? ????????? ????????? ???????????? ?????? ID"),
                                fieldWithPath("profileImage.profileImageUrl").type(STRING).description("????????? ????????? ????????? ????????? ????????? ???????????? URL"),
                                fieldWithPath("profileImage.profileImageThumbnailUrl").type(STRING).description("????????? ????????? ????????? ????????? ???????????? ????????? ????????? ???????????? URL"),
                                fieldWithPath("nickname").type(STRING).description("????????? ????????? ?????????"),
                                fieldWithPath("description").type(STRING).description("????????? ????????? ????????????"),
                                fieldWithPath("roomEscapeStatus.challengesCount").type(NUMBER).description("????????? ????????? ????????? ????????? ??? ??????"),
                                fieldWithPath("roomEscapeStatus.successCount").type(NUMBER).description("????????? ????????? ?????? ???????????? ????????? ??? ??????"),
                                fieldWithPath("roomEscapeStatus.failCount").type(NUMBER).description("????????? ????????? ?????? ???????????? ????????? ??? ??????"),
                                fieldWithPath("roomEscapeRecodesOpenStatus").type(STRING).description("????????? ????????? ????????? ?????? ??????"),
                                fieldWithPath("playInclinations").type(ARRAY).description("????????? ????????? ????????? ??????"),
                                fieldWithPath("playInclinations[].genre").type(STRING).description("????????? ????????? ???????????? ?????? +\n" +
                                        generateLinkCode(GENRE)),
                                fieldWithPath("playInclinations[].playCount").type(NUMBER).description("????????? ????????? ?????? ?????? ???????????? ??????"),
                                fieldWithPath("myProfile").type(BOOLEAN).description("????????? ???????????? ??????????????? ?????? +\n" +
                                        "????????? ???????????? ????????? ?????? ????????? ???????????? ??????????????? ??????")
                        )
                ))
        ;

    }

}