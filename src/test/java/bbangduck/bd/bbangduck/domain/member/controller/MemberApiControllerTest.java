package bbangduck.bd.bbangduck.domain.member.controller;

import bbangduck.bd.bbangduck.domain.auth.controller.dto.MemberSocialSignUpRequestDto;
import bbangduck.bd.bbangduck.domain.auth.service.dto.TokenDto;
import bbangduck.bd.bbangduck.domain.file.entity.FileStorage;
import bbangduck.bd.bbangduck.domain.member.controller.dto.MemberUpdateDescriptionRequestDto;
import bbangduck.bd.bbangduck.domain.member.controller.dto.MemberUpdateNicknameRequestDto;
import bbangduck.bd.bbangduck.domain.member.controller.dto.MemberUpdateProfileImageRequestDto;
import bbangduck.bd.bbangduck.domain.member.entity.enumerate.SocialType;
import bbangduck.bd.bbangduck.domain.member.exception.MemberNicknameDuplicateException;
import bbangduck.bd.bbangduck.domain.member.exception.MemberProfileImageNotFoundException;
import bbangduck.bd.bbangduck.domain.member.service.dto.MemberProfileImageDto;
import bbangduck.bd.bbangduck.global.common.ResponseStatus;
import bbangduck.bd.bbangduck.member.BaseJGMApiControllerTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class MemberApiControllerTest extends BaseJGMApiControllerTest {

    @Test
    @DisplayName("회원 프로필 조회 테스트")
    public void getMemberProfileTest() throws Exception{
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
        MemberSocialSignUpRequestDto memberSocialSignUpRequestDto = MemberSocialSignUpRequestDto.builder()
                .email("test@email.com")
                .nickname("testNickname")

                .socialType(SocialType.KAKAO)
                .socialId("3213123")
                .build();
        Long signUpMemberId = authenticationService.signUp(memberSocialSignUpRequestDto.toServiceDto());

        TokenDto tokenDto = authenticationService.signIn(signUpMemberId);
        String totalAccessToken = tokenDto.getAccessToken();

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
                get("/api/members/" + signUpMemberId)
                        .header(securityJwtProperties.getJwtTokenHeader(), totalAccessToken)
        ).andDo(print());

        //when

        //then

    }

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
                .andExpect(jsonPath("status").value(ResponseStatus.MEMBER_UPDATE_PROFILE_IMAGE_SUCCESS.getStatus()))
                .andExpect(jsonPath("data").doesNotExist())
                .andExpect(jsonPath("message").value(ResponseStatus.MEMBER_UPDATE_PROFILE_IMAGE_SUCCESS.getMessage()))
                .andDo(document(
                        "update-profile-image-success",
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
                                fieldWithPath("data").description("[null]"),
                                fieldWithPath("message").description(MESSAGE_DESCRIPTION)
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
                .andExpect(jsonPath("status").value(ResponseStatus.MEMBER_UPDATE_PROFILE_IMAGE_SUCCESS.getStatus()))
                .andExpect(jsonPath("data").doesNotExist())
                .andExpect(jsonPath("message").value(ResponseStatus.MEMBER_UPDATE_PROFILE_IMAGE_SUCCESS.getMessage()))
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
                .andExpect(jsonPath("status").value(ResponseStatus.MEMBER_DELETE_PROFILE_IMAGE_SUCCESS.getStatus()))
                .andExpect(jsonPath("data").doesNotExist())
                .andExpect(jsonPath("message").value(ResponseStatus.MEMBER_DELETE_PROFILE_IMAGE_SUCCESS.getMessage()))
                .andDo(document(
                        "delete-profile-image-success",
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
                .andExpect(jsonPath("status").value(ResponseStatus.MEMBER_UPDATE_NICKNAME_SUCCESS.getStatus()))
                .andExpect(jsonPath("data").doesNotExist())
                .andExpect(jsonPath("message").value(ResponseStatus.MEMBER_UPDATE_NICKNAME_SUCCESS.getMessage()))
                .andDo(document(
                        "update-nickname-success",
                        requestHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("[application/json;charset=UTF-8] 지정"),
                                headerWithName(securityJwtProperties.getJwtTokenHeader()).description(JWT_TOKEN_HEADER_DESCRIPTION)
                        ),
                        requestFields(
                                fieldWithPath("nickname").description("변경할 Nickname")
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
                .andExpect(jsonPath("status").value(ResponseStatus.MEMBER_UPDATE_DESCRIPTION_SUCCESS.getStatus()))
                .andExpect(jsonPath("data").doesNotExist())
                .andExpect(jsonPath("message").value(ResponseStatus.MEMBER_UPDATE_DESCRIPTION_SUCCESS.getMessage()))
                .andDo(document(
                        "update-description-success",
                        requestHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("[application/json;charset=UTF-8] 지정"),
                                headerWithName(securityJwtProperties.getJwtTokenHeader()).description(JWT_TOKEN_HEADER_DESCRIPTION)
                        ),
                        requestFields(
                                fieldWithPath("description").description("변경할 자기소개 기입")
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
    public void toggleRoomEscapeRecodesOpenTest() throws Exception {
        //given
        MemberSocialSignUpRequestDto memberSocialSignUpRequestDto = createMemberSocialSignUpRequestDto();
        Long signUpId = authenticationService.signUp(memberSocialSignUpRequestDto.toServiceDto());

        TokenDto tokenDto = authenticationService.signIn(signUpId);

        //when
        ResultActions perform = mockMvc.perform(
                put("/api/members/" + signUpId + "/room-escape/recodes/open-yn")
                        .header(securityJwtProperties.getJwtTokenHeader(), tokenDto.getAccessToken())
        ).andDo(print());

        //then
        perform
                .andExpect(status().isNoContent())
                .andExpect(jsonPath("status").value(ResponseStatus.MEMBER_TOGGLE_ROOM_ESCAPE_RECODES_OPEN_SUCCESS.getStatus()))
                .andExpect(jsonPath("data").doesNotExist())
                .andExpect(jsonPath("message").value(ResponseStatus.MEMBER_TOGGLE_ROOM_ESCAPE_RECODES_OPEN_SUCCESS.getMessage()))
                .andDo(document(
                        "toggle-room-escape-recodes-open-yn-success",
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
    @DisplayName("회원 방탈출 기록 공개 여부 변경 - 다른 회원의 방탈출 기록 공개 여부 변경")
    public void toggleRoomEscapeRecodesOpen_DifferentMember() throws Exception {
          //given
        MemberSocialSignUpRequestDto memberSocialSignUpRequestDto = createMemberSocialSignUpRequestDto();
        Long signUpId = authenticationService.signUp(memberSocialSignUpRequestDto.toServiceDto());

        TokenDto tokenDto = authenticationService.signIn(signUpId);

        //when
        ResultActions perform = mockMvc.perform(
                put("/api/members/" + 10000L + "/room-escape/recodes/open-yn")
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
    @DisplayName("회원 방탈출 기록 공개 여부 변경 - 인증되지 않았을 경우")
    public void toggleRoomEscapeRecodesOpen_Unauthorized() throws Exception {
          //given
        MemberSocialSignUpRequestDto memberSocialSignUpRequestDto = createMemberSocialSignUpRequestDto();
        Long signUpId = authenticationService.signUp(memberSocialSignUpRequestDto.toServiceDto());

        TokenDto tokenDto = authenticationService.signIn(signUpId);

        //when
        ResultActions perform = mockMvc.perform(
                put("/api/members/" + signUpId + "/room-escape/recodes/open-yn")
//                        .header(securityJwtProperties.getJwtTokenHeader(), tokenDto.getAccessToken())
        ).andDo(print());


        //then
        perform
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("status").value(ResponseStatus.UNAUTHORIZED.getStatus()))
                .andExpect(jsonPath("data").doesNotExist())
                .andExpect(jsonPath("message").value(ResponseStatus.UNAUTHORIZED.getMessage()));

    }


}