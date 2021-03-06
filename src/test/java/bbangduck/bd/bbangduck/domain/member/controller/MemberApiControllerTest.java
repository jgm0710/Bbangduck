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

@DisplayName("회원(프로필 관리) 관련 API Controller 테스트")
class MemberApiControllerTest extends BaseJGMApiControllerTest {

    @MockBean
    KakaoSignInService kakaoSignInService;

    @Test
    @DisplayName("회원 프로필 이미지 수정")
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
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("[application/json;charset=UTF-8] 지정"),
                                headerWithName(securityJwtProperties.getJwtTokenHeader()).description(JWT_TOKEN_HEADER_DESCRIPTION)
                        ),
                        requestFields(
                                fieldWithPath("fileStorageId").description("변경할 이미지 파일의 파일 저장소 ID"),
                                fieldWithPath("fileName").description("변경할 이미지 파일의 이름")
                        )
                ))
        ;

    }

    @Test
    @DisplayName("인증되지 않은 회원이 프로필 이미지를 변경하는 경우")
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
    @DisplayName("회원 프로필 이미지 수정 시 다른 회원의 프로필 이미지를 수정하는 경우")
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
    @DisplayName("회원 프로필 이미지 수정 - 기존에 프로필 이미지가 있었던 경우")
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
    @DisplayName("회원 프로필 이미지 수정 - 파일 정보를 기입하지 않은 경우")
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
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("[application/json;charset=UTF-8] 지정"),
                                headerWithName(securityJwtProperties.getJwtTokenHeader()).description(JWT_TOKEN_HEADER_DESCRIPTION)
                        ),
                        requestFields(
                                fieldWithPath("fileStorageId").description("변경할 이미지 파일의 파일 저장소 ID"),
                                fieldWithPath("fileName").description("변경할 이미지 파일의 이름")
                        ),
                        responseFields(
                                fieldWithPath("status").description(STATUS_DESCRIPTION),
                                fieldWithPath("data[0].objectName").description("예외가 발생한 대상 객체의 이름"),
                                fieldWithPath("data[0].code").description("예외 코드"),
                                fieldWithPath("data[0].defaultMessage").description("발생한 예외에 대한 메세지"),
                                fieldWithPath("data[0].field").description("예외가 발생한 대상 필드의 이름"),
                                fieldWithPath("message").description(MESSAGE_DESCRIPTION)
                        )
                ))
        ;

    }


    @Test
    @DisplayName("회원 프로필 이미지 삭제")
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
    @DisplayName("회원 프로필 이미지 삭제 - 다른 회원의 프로필 이미지 삭제")
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
    @DisplayName("회원 프로필 이미지 삭제 - 인증되지 않은 경우")
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
    @DisplayName("회원 프로필 이미지 삭제 - 해당 회원의 프로필 이미지가 원래 없는 경우")
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
    @DisplayName("회원 닉네임 변경")
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
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("[application/json;charset=UTF-8] 지정"),
                                headerWithName(securityJwtProperties.getJwtTokenHeader()).description(JWT_TOKEN_HEADER_DESCRIPTION)
                        ),
                        requestFields(
                                fieldWithPath("nickname").description("변경할 Nickname")
                        )
                ))
        ;

    }

    @Test
    @DisplayName("회원 닉네임 수정 - 인증되지 않은 경우")
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
    @DisplayName("회원 닉네임 변경 - 닉네임을 기입하지 않은 경우")
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
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("[application/json;charset=UTF-8] 지정"),
                                headerWithName(securityJwtProperties.getJwtTokenHeader()).description(JWT_TOKEN_HEADER_DESCRIPTION)
                        ),
                        requestFields(
                                fieldWithPath("nickname").description("변경할 Nickname")
                        ),
                        responseFields(
                                fieldWithPath("status").description(STATUS_DESCRIPTION),
                                fieldWithPath("data[0].objectName").description("예외가 발생한 객체의 이름"),
                                fieldWithPath("data[0].code").description("예외 코드"),
                                fieldWithPath("data[0].defaultMessage").description("발생한 예외에 대한 메세지"),
                                fieldWithPath("data[0].field").description("예외가 발생한 필드의 이름"),
                                fieldWithPath("message").description(MESSAGE_DESCRIPTION)
                        )
                ))
        ;

    }

    @Test
    @DisplayName("회원 닉네임 변경 - 다른 회원의 닉네임을 변경하는 경우")
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
    @DisplayName("회원 닉네임 수정 - 다른 회원의 Nickname 과 중복되는 경우")
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
    @DisplayName("회원 자기소개 수정")
    public void updateDescriptionTest() throws Exception {
        //given
        MemberSocialSignUpRequestDto memberSocialSignUpRequestDto = createMemberSocialSignUpRequestDto();
        Long signUpId = authenticationService.signUp(memberSocialSignUpRequestDto.toServiceDto());

        memberService.updateDescription(signUpId, "첫 자기 소개 등록");

        TokenDto tokenDto = authenticationService.signIn(signUpId);

        MemberUpdateDescriptionRequestDto memberUpdateDescriptionRequestDto = new MemberUpdateDescriptionRequestDto("새로운 자기 소개 등록");

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
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("[application/json;charset=UTF-8] 지정"),
                                headerWithName(securityJwtProperties.getJwtTokenHeader()).description(JWT_TOKEN_HEADER_DESCRIPTION)
                        ),
                        requestFields(
                                fieldWithPath("description").description("변경할 자기소개 기입")
                        )
                ))
        ;

    }


    @Test
    @DisplayName("회원 자기소개 수정 - 자기소개를 기입하지 않은 경우")
    public void updateDescription_Empty() throws Exception {
        //given
        MemberSocialSignUpRequestDto memberSocialSignUpRequestDto = createMemberSocialSignUpRequestDto();
        Long signUpId = authenticationService.signUp(memberSocialSignUpRequestDto.toServiceDto());

        memberService.updateDescription(signUpId, "첫 자기 소개 등록");

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
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("[application/json;charset=UTF-8] 지정"),
                                headerWithName(securityJwtProperties.getJwtTokenHeader()).description(JWT_TOKEN_HEADER_DESCRIPTION)
                        ),
                        requestFields(
                                fieldWithPath("description").description("변경할 자기소개 기입")
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
    @DisplayName("회원 자기소개 수정 - 인증되지 않은 경우")
    public void updateDescription_Unauthorized() throws Exception {
        //given
        MemberSocialSignUpRequestDto memberSocialSignUpRequestDto = createMemberSocialSignUpRequestDto();
        Long signUpId = authenticationService.signUp(memberSocialSignUpRequestDto.toServiceDto());

        memberService.updateDescription(signUpId, "첫 자기 소개 등록");

        TokenDto tokenDto = authenticationService.signIn(signUpId);

        MemberUpdateDescriptionRequestDto memberUpdateDescriptionRequestDto = new MemberUpdateDescriptionRequestDto("변경할 자기소개");

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
    @DisplayName("회원 자기소개 수정 - 다른 회원의 자기소개 수정")
    public void updateDescription_DifferentMember() throws Exception {
        //given
        MemberSocialSignUpRequestDto memberSocialSignUpRequestDto = createMemberSocialSignUpRequestDto();
        Long signUpId = authenticationService.signUp(memberSocialSignUpRequestDto.toServiceDto());

        memberService.updateDescription(signUpId, "첫 자기 소개 등록");

        TokenDto tokenDto = authenticationService.signIn(signUpId);

        MemberUpdateDescriptionRequestDto memberUpdateDescriptionRequestDto = new MemberUpdateDescriptionRequestDto("변경할 자기소개");

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
    @DisplayName("회원 방탈출 기록 공개 여부 변경")
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
                                fieldWithPath("roomEscapeRecodesOpenStatus").description("변경할 방탈출 기록 공개 상태 기입 +\n" +
                                        generateLinkCode(MEMBER_ROOM_ESCAPE_RECODES_OPEN_STATUS))
                        )
                ))
        ;

    }

    @Test
    @DisplayName("회원 방탈출 기록 공개 여부 변경 - 다른 회원의 방탈출 기록 공개 여부 변경")
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
    @DisplayName("회원 방탈출 기록 공개 여부 변경 - 인증되지 않았을 경우")
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
    @DisplayName("회원 프로필 조회 - 다른 회원의 프로필을 조회하는 경우")
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
                                fieldWithPath("memberId").type(NUMBER).description("조회된 회원의 식별 ID"),
                                fieldWithPath("profileImage.profileImageId").type(NUMBER).description("조회된 회원에게 등록된 프로필 이미지의 식별 ID \n" +
                                        "프로필 이미지 삭제 요청 등에 사용될 수 있음"),
                                fieldWithPath("profileImage.profileImageUrl").type(STRING).description("조회된 회원에게 등록된 프로필 이미지 다운로드 URL"),
                                fieldWithPath("profileImage.profileImageThumbnailUrl").type(STRING).description("조회된 회원에게 등록된 프로필 이미지의 썸네일 이미지 다운로드 URL"),
                                fieldWithPath("nickname").type(STRING).description("조회된 회원의 닉네임"),
                                fieldWithPath("description").type(STRING).description("조회된 회원의 자기소개"),
                                fieldWithPath("roomEscapeStatus.challengesCount").type(NUMBER).description("조회된 회원이 테마에 도전한 총 횟수 +\n" +
                                        "(리뷰를 작성한 횟수라고 생각해도 무방)"),
                                fieldWithPath("roomEscapeStatus.successCount").type(NUMBER).description("조회된 회원이 테마 클리어에 성공한 횟수"),
                                fieldWithPath("roomEscapeStatus.failCount").type(NUMBER).description("조회된 회원이 테마 클리어에 실패한 횟수"),
                                fieldWithPath("roomEscapeRecodesOpenStatus").type(STRING).description("조회된 회원의 방탈출 기록 공개 상태 +\n" +
                                        generateLinkCode(MEMBER_ROOM_ESCAPE_RECODES_OPEN_STATUS)),
                                fieldWithPath("playInclinations").type(ARRAY).description("조회된 회원의 플레이 성향"),
                                fieldWithPath("playInclinations[].genre").type(STRING).description("조회된 회원의 플레이 성향에 등록된 장르 +\n" +
                                        generateLinkCode(GENRE)),
                                fieldWithPath("playInclinations[].playCount").type(NUMBER).description("조회된 회원의 플레이 성항에 등록된 장르를 플레이한 횟수"),
                                fieldWithPath("myProfile").description("자신의 프로필을 조회했는지 여부")
                        )
                ))
        ;

    }

    @Test
    @DisplayName("회원 프로필 조회 - 자신의 프로필을 조회하는 경우")
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
                                fieldWithPath("memberId").description("조회된 회원의 식별 ID"),
                                fieldWithPath("profileImage.profileImageId").description("조회된 회원에게 등록된 프로필 이미지의 식별 ID \n" +
                                        "프로필 이미지 삭제 요청 등에 사용될 수 있음"),
                                fieldWithPath("profileImage.profileImageUrl").description("조회된 회원에게 등록된 프로필 이미지 다운로드 URL"),
                                fieldWithPath("profileImage.profileImageThumbnailUrl").description("조회된 회원에게 등록된 프로필 이미지의 썸네일 이미지 다운로드 URL"),
                                fieldWithPath("nickname").description("조회된 회원의 닉네임"),
                                fieldWithPath("description").description("조회된 회원의 자기소개"),
                                fieldWithPath("roomEscapeStatus.challengesCount").description("조회된 회원이 테마에 도전한 총 횟수 +\n" +
                                        "(리뷰를 작성한 횟수라고 생각해도 무방)"),
                                fieldWithPath("roomEscapeStatus.successCount").description("조회된 회원이 테마 클리어에 성공한 횟수"),
                                fieldWithPath("roomEscapeStatus.failCount").description("조회된 회원이 테마 클리어에 실패한 횟수"),
                                fieldWithPath("roomEscapeRecodesOpenStatus").description("조회된 회원의 방탈출 기록 공개 상태 +\n" +
                                        generateLinkCode(MEMBER_ROOM_ESCAPE_RECODES_OPEN_STATUS)),
                                fieldWithPath("playInclinations").description("조회된 회원의 플레이 성향"),
                                fieldWithPath("playInclinations[].genre").description("조회된 회원의 플레이 성향에 등록된 장르 +\n" +
                                        generateLinkCode(GENRE)),
                                fieldWithPath("playInclinations[].playCount").description("조회된 회원의 플레이 성항에 등록된 장르를 플레이한 횟수"),
                                fieldWithPath("email").description("조회된 회원의 Email"),
                                fieldWithPath("socialAccounts[0].socialId").description("조회된 회원에 등록된 소셜 계정의 (여러개 일 수 있습니다.) 식별 ID (소셜 매체에서 응답된 회원 식별 ID)"),
                                fieldWithPath("socialAccounts[0].socialType").description("조회된 회원에 등록된 소셜 계정의 (여러개 일 수 있습니다.) 타입 (어떤 소셜 매체를 통해 등록되었는지)"),
                                fieldWithPath("registerTimes").description("조회된 회원의 생성 일자 (가입일시)"),
                                fieldWithPath("updateTimes").description("조회된 회원의 수정 일자 (마지막으로 개인정보가 수정된 일시)"),
                                fieldWithPath("myProfile").description("자신의 프로필을 조회했는지 여부")
                        )
                ))
        ;

    }

    @Test
    @DisplayName("회원 프로필 조회 - 탈퇴된 회원을 다른 회원이 조회하는 경우")
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
    @DisplayName("인증되지 않은 회원이 회원 프로필을 조회하는 경우")
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
    @DisplayName("회원 조회 시 해당 회원을 찾을 수 없는 경우")
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
    @DisplayName("탈퇴한 회원이 자신의 프로필을 조회하는 경우")
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
    @DisplayName("회원의 플레이 성향 조회")
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
                                fieldWithPath("playInclinations").type(ARRAY).description("회원의 플레이 성향"),
                                fieldWithPath("playInclinations[].genre").type(STRING).description("회원의 플레이 성향에 등록된 장르 +\n" +
                                        generateLinkCode(GENRE)),
                                fieldWithPath("playInclinations[].playCount").type(NUMBER).description("회원이 플레이 성향에 등록된 장르를 플레이한 횟수"),
                                fieldWithPath("totalThemeEvaluatesCount").type(NUMBER).description("회원이 테마를 평가한 총 횟수")
                        )
                ))
        ;

    }

    @Test
    @DisplayName("회원 검색 - 닉네임으로 회원 검색")
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
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("[application/json;charset=UTF-8] 지정"),
                                headerWithName(securityJwtProperties.getJwtTokenHeader()).description(JWT_TOKEN_HEADER_DESCRIPTION)
                        ),
                        requestFields(
                                fieldWithPath("searchType").description("회원 검색 시 이메일을 통해 검색할 것인지, 닉네임을 통해 검색할 것인지 기입 +\n" +
                                        generateLinkCode(MEMBER_SEARCH_KEYWORD_TYPE)),
                                fieldWithPath("keyword").description("회원 검색 시 필요한 키워드 +\n" +
                                        "정확한 이메일, 닉네임을 지정해야 검색할 수 있습니다.")
                        ),
                        responseFields(
                                fieldWithPath("memberId").type(NUMBER).description("검색된 회원의 식별 ID"),
                                fieldWithPath("profileImage.profileImageId").type(NUMBER).description("검색된 회원에 등록된 프로필 이미지의 식별 ID"),
                                fieldWithPath("profileImage.profileImageUrl").type(STRING).description("검색된 회원에 등록된 프로필 이미지 다운로드 URL"),
                                fieldWithPath("profileImage.profileImageThumbnailUrl").type(STRING).description("검색된 회원에 등록횐 프로필 이미지의 썸네일 이미지 다운로드 URL"),
                                fieldWithPath("nickname").type(STRING).description("검색된 회원의 닉네임"),
                                fieldWithPath("description").type(STRING).description("검색된 회원의 자기소개"),
                                fieldWithPath("roomEscapeStatus.challengesCount").type(NUMBER).description("검색된 회원이 테마에 도전한 총 횟수"),
                                fieldWithPath("roomEscapeStatus.successCount").type(NUMBER).description("검색된 회원이 테마 클리어에 성공한 총 횟수"),
                                fieldWithPath("roomEscapeStatus.failCount").type(NUMBER).description("검색된 회원이 테마 클리어에 실패한 총 횟수"),
                                fieldWithPath("roomEscapeRecodesOpenStatus").type(STRING).description("검색된 회원의 방탈출 공개 상태"),
                                fieldWithPath("playInclinations").type(ARRAY).description("검색된 회원이 플레이 성향"),
                                fieldWithPath("playInclinations[].genre").type(STRING).description("검색된 회원이 플레이한 장르 +\n" +
                                        generateLinkCode(GENRE)),
                                fieldWithPath("playInclinations[].playCount").type(NUMBER).description("검색된 회원이 해당 장르 플레이한 횟수"),
                                fieldWithPath("myProfile").type(BOOLEAN).description("자신의 프로필을 조회했는지 여부 +\n" +
                                        "자신의 프로필은 조회한 경우 민감한 정보들이 추가적으로 응답")
                        )
                ))
        ;

    }

}