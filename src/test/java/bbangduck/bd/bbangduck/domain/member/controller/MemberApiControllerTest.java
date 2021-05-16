package bbangduck.bd.bbangduck.domain.member.controller;

import bbangduck.bd.bbangduck.domain.auth.controller.dto.MemberSocialSignUpRequestDto;
import bbangduck.bd.bbangduck.domain.auth.service.dto.TokenDto;
import bbangduck.bd.bbangduck.domain.file.entity.FileStorage;
import bbangduck.bd.bbangduck.domain.member.controller.dto.MemberUpdateProfileRequestDto;
import bbangduck.bd.bbangduck.domain.member.entity.SocialType;
import bbangduck.bd.bbangduck.global.common.ResponseStatus;
import bbangduck.bd.bbangduck.member.BaseJGMApiControllerTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
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
        MemberSocialSignUpRequestDto memberSocialSignUpRequestDto = MemberSocialSignUpRequestDto.builder()
                .email("test@email.com")
                .nickname("testNickname")

                .socialType(SocialType.KAKAO)
                .socialId("3213123")
                .build();
        Long signUpMemberId = authenticationService.signUp(memberSocialSignUpRequestDto.toServiceDto());

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
        MemberSocialSignUpRequestDto memberSocialSignUpRequestDto = MemberSocialSignUpRequestDto.builder()
                .email("test@email.com")
                .nickname("testNickname")

                .socialType(SocialType.KAKAO)
                .socialId("3213123")
                .build();
        Long signUpMemberId = authenticationService.signUp(memberSocialSignUpRequestDto.toServiceDto());

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
        MemberSocialSignUpRequestDto memberSocialSignUpRequestDto = MemberSocialSignUpRequestDto.builder()
                .email("test@email.com")
                .nickname("testNickname")

                .socialType(SocialType.KAKAO)
                .socialId("3213123")
                .build();
        Long signUpMemberId = authenticationService.signUp(memberSocialSignUpRequestDto.toServiceDto());

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
        MemberSocialSignUpRequestDto memberSocialSignUpRequestDto = MemberSocialSignUpRequestDto.builder()
                .email("test@email.com")
                .nickname("testNickname")

                .socialType(SocialType.KAKAO)
                .socialId("3213123")
                .build();
        Long signUpMemberId = authenticationService.signUp(memberSocialSignUpRequestDto.toServiceDto());

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

    @Test
    @DisplayName("회원 수정 테스트")
    public void updateProfile() throws Exception {
        //given
        MemberSocialSignUpRequestDto memberSignUpDto = createMemberSocialSignUpRequestDto();
        Long savedMemberId = authenticationService.signUp(memberSignUpDto.toServiceDto());

        MockMultipartFile multipartFile = createMockMultipartFile("files", IMAGE_FILE_CLASS_PATH);
        Long uploadedFileId = fileStorageService.uploadImageFile(multipartFile);
        FileStorage storedFile = fileStorageService.getStoredFile(uploadedFileId);

        MemberUpdateProfileRequestDto memberUpdateProfileRequestDto = createMemberUpdateProfileRequestDto(storedFile.getId(), storedFile.getFileName());

        TokenDto tokenDto = authenticationService.signIn(savedMemberId);

        //when
        ResultActions perform = mockMvc.perform(
                put("/api/members/" + savedMemberId)
                        .header(securityJwtProperties.getJwtTokenHeader(), tokenDto.getTotalAccessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(memberUpdateProfileRequestDto))
        ).andDo(print());

        //then
        perform
                .andExpect(status().isOk())
                .andExpect(jsonPath("status").value(ResponseStatus.MEMBER_MODIFY_PROFILE_SUCCESS.getStatus()))
                .andExpect(jsonPath("data").doesNotExist())
                .andExpect(jsonPath("message").value(ResponseStatus.MEMBER_MODIFY_PROFILE_SUCCESS.getMessage()))
        ;
    }

    @Test
    @DisplayName("회원 수정 시 회원을 찾을 수 없는 경우")
    public void updateProfile_NotFound() throws Exception {
         //given
        MemberSocialSignUpRequestDto memberSignUpDto = createMemberSocialSignUpRequestDto();
        Long savedMemberId = authenticationService.signUp(memberSignUpDto.toServiceDto());

        MockMultipartFile multipartFile = createMockMultipartFile("files", IMAGE_FILE_CLASS_PATH);
        Long uploadedFileId = fileStorageService.uploadImageFile(multipartFile);
        FileStorage storedFile = fileStorageService.getStoredFile(uploadedFileId);

        MemberUpdateProfileRequestDto memberUpdateProfileRequestDto = createMemberUpdateProfileRequestDto(storedFile.getId(), storedFile.getFileName());

        TokenDto tokenDto = authenticationService.signIn(savedMemberId);

        //when
        ResultActions perform = mockMvc.perform(
                put("/api/members/" + 10000L)
                        .header(securityJwtProperties.getJwtTokenHeader(), tokenDto.getTotalAccessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(memberUpdateProfileRequestDto))
        ).andDo(print());

        //then
        perform
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("status").value(ResponseStatus.MEMBER_NOT_FOUND.getStatus()))
                .andExpect(jsonPath("data").doesNotExist())
                .andExpect(jsonPath("message").value(ResponseStatus.MEMBER_NOT_FOUND.getMessage()));

    }

    // TODO: 2021-05-17 여기서부터 다시 진행
    @Test
    @DisplayName("회원 수정 시 다른 회원의 프로필을 수정하는 경우")
    public void updateProfile_DifferentMember() throws Exception {
         //given
        MemberSocialSignUpRequestDto memberSignUpDto = createMemberSocialSignUpRequestDto();
        Long savedMemberId = authenticationService.signUp(memberSignUpDto.toServiceDto());

        MockMultipartFile multipartFile = createMockMultipartFile("files", IMAGE_FILE_CLASS_PATH);
        Long uploadedFileId = fileStorageService.uploadImageFile(multipartFile);
        FileStorage storedFile = fileStorageService.getStoredFile(uploadedFileId);

        MemberUpdateProfileRequestDto memberUpdateProfileRequestDto = createMemberUpdateProfileRequestDto(storedFile.getId(), storedFile.getFileName());

        TokenDto tokenDto = authenticationService.signIn(savedMemberId);

        //when
        ResultActions perform = mockMvc.perform(
                put("/api/members/" + savedMemberId)
                        .header(securityJwtProperties.getJwtTokenHeader(), tokenDto.getTotalAccessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(memberUpdateProfileRequestDto))
        ).andDo(print());
        
        //then
        perform
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("status").value(ResponseStatus.UPDATE_DIFFERENT_MEMBER_PROFILE.getStatus()))
                .andExpect(jsonPath("data").doesNotExist())
                .andExpect(jsonPath("message").value(ResponseStatus.UPDATE_DIFFERENT_MEMBER_PROFILE.getMessage()));

    }

    @Test
    @DisplayName("회원 수정 시 닉테임을 기입하지 않은 경우")
    public void updateProfile_NicknameBlank() throws Exception {
         //given
        MemberSocialSignUpRequestDto memberSignUpDto = createMemberSocialSignUpRequestDto();
        Long savedMemberId = authenticationService.signUp(memberSignUpDto.toServiceDto());

        MockMultipartFile multipartFile = createMockMultipartFile("files", IMAGE_FILE_CLASS_PATH);
        Long uploadedFileId = fileStorageService.uploadImageFile(multipartFile);
        FileStorage storedFile = fileStorageService.getStoredFile(uploadedFileId);

        MemberUpdateProfileRequestDto memberUpdateProfileRequestDto = createMemberUpdateProfileRequestDto(storedFile.getId(), storedFile.getFileName());

        TokenDto tokenDto = authenticationService.signIn(savedMemberId);

        //when
        ResultActions perform = mockMvc.perform(
                put("/api/members/" + savedMemberId)
                        .header(securityJwtProperties.getJwtTokenHeader(), tokenDto.getTotalAccessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(memberUpdateProfileRequestDto))
        ).andDo(print());
        
        //then
        perform
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("status").value(ResponseStatus.UPDATE_PROFILE_NOT_VALID.getStatus()))
                .andExpect(jsonPath("data").exists())
                .andExpect(jsonPath("message").value(ResponseStatus.UPDATE_PROFILE_NOT_VALID.getMessage()));

    }

    @Test
    @DisplayName("회원 수정 시 자기 소개를 1000자 이상 기입한 경우")
    public void updateProfile_OverDescription() throws Exception {
         //given
        MemberSocialSignUpRequestDto memberSignUpDto = createMemberSocialSignUpRequestDto();
        Long savedMemberId = authenticationService.signUp(memberSignUpDto.toServiceDto());

        MockMultipartFile multipartFile = createMockMultipartFile("files", IMAGE_FILE_CLASS_PATH);
        Long uploadedFileId = fileStorageService.uploadImageFile(multipartFile);
        FileStorage storedFile = fileStorageService.getStoredFile(uploadedFileId);

        MemberUpdateProfileRequestDto memberUpdateProfileRequestDto = createMemberUpdateProfileRequestDto(storedFile.getId(), storedFile.getFileName());

        TokenDto tokenDto = authenticationService.signIn(savedMemberId);

        //when
        ResultActions perform = mockMvc.perform(
                put("/api/members/" + savedMemberId)
                        .header(securityJwtProperties.getJwtTokenHeader(), tokenDto.getTotalAccessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(memberUpdateProfileRequestDto))
        ).andDo(print());
        
        //then

    }

    @Test
    @DisplayName("회원 수정 시 프로필 이미지 정보를 모두 기입하지 않은 경우")
    public void updateProfile_ProfileImageInfoIsEmpty() throws Exception {
         //given
        MemberSocialSignUpRequestDto memberSignUpDto = createMemberSocialSignUpRequestDto();
        Long savedMemberId = authenticationService.signUp(memberSignUpDto.toServiceDto());

        MockMultipartFile multipartFile = createMockMultipartFile("files", IMAGE_FILE_CLASS_PATH);
        Long uploadedFileId = fileStorageService.uploadImageFile(multipartFile);
        FileStorage storedFile = fileStorageService.getStoredFile(uploadedFileId);

        MemberUpdateProfileRequestDto memberUpdateProfileRequestDto = createMemberUpdateProfileRequestDto(storedFile.getId(), storedFile.getFileName());

        TokenDto tokenDto = authenticationService.signIn(savedMemberId);

        //when
        ResultActions perform = mockMvc.perform(
                put("/api/members/" + savedMemberId)
                        .header(securityJwtProperties.getJwtTokenHeader(), tokenDto.getTotalAccessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(memberUpdateProfileRequestDto))
        ).andDo(print());

        //when

        //then

    }

    @Test
    @DisplayName("회원 프로필 수정 시 이미지 파일 ID 만 기입한 경우")
    public void updateProfile_ProfileImageNameIsBlank() throws Exception {
         //given
        MemberSocialSignUpRequestDto memberSignUpDto = createMemberSocialSignUpRequestDto();
        Long savedMemberId = authenticationService.signUp(memberSignUpDto.toServiceDto());

        MockMultipartFile multipartFile = createMockMultipartFile("files", IMAGE_FILE_CLASS_PATH);
        Long uploadedFileId = fileStorageService.uploadImageFile(multipartFile);
        FileStorage storedFile = fileStorageService.getStoredFile(uploadedFileId);

        MemberUpdateProfileRequestDto memberUpdateProfileRequestDto = createMemberUpdateProfileRequestDto(storedFile.getId(), storedFile.getFileName());

        TokenDto tokenDto = authenticationService.signIn(savedMemberId);

        //when
        ResultActions perform = mockMvc.perform(
                put("/api/members/" + savedMemberId)
                        .header(securityJwtProperties.getJwtTokenHeader(), tokenDto.getTotalAccessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(memberUpdateProfileRequestDto))
        ).andDo(print());

        //when

        //then

    }

    @Test
    @DisplayName("회원 프로필 수정 시 이미지 파일명만 기입한 경우")
    public void updateProfile_ProfileImageIdIsNull() throws Exception {
         //given
        MemberSocialSignUpRequestDto memberSignUpDto = createMemberSocialSignUpRequestDto();
        Long savedMemberId = authenticationService.signUp(memberSignUpDto.toServiceDto());

        MockMultipartFile multipartFile = createMockMultipartFile("files", IMAGE_FILE_CLASS_PATH);
        Long uploadedFileId = fileStorageService.uploadImageFile(multipartFile);
        FileStorage storedFile = fileStorageService.getStoredFile(uploadedFileId);

        MemberUpdateProfileRequestDto memberUpdateProfileRequestDto = createMemberUpdateProfileRequestDto(storedFile.getId(), storedFile.getFileName());

        TokenDto tokenDto = authenticationService.signIn(savedMemberId);

        //when
        ResultActions perform = mockMvc.perform(
                put("/api/members/" + savedMemberId)
                        .header(securityJwtProperties.getJwtTokenHeader(), tokenDto.getTotalAccessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(memberUpdateProfileRequestDto))
        ).andDo(print());

        //when

        //then

    }

    @Test
    @DisplayName("회원 프로필 수정 시 Nickname 이 중복될 경우")
    public void updateProfile_NicknameDuplicate() throws Exception {
         //given
        MemberSocialSignUpRequestDto memberSignUpDto = createMemberSocialSignUpRequestDto();
        Long savedMemberId = authenticationService.signUp(memberSignUpDto.toServiceDto());

        MockMultipartFile multipartFile = createMockMultipartFile("files", IMAGE_FILE_CLASS_PATH);
        Long uploadedFileId = fileStorageService.uploadImageFile(multipartFile);
        FileStorage storedFile = fileStorageService.getStoredFile(uploadedFileId);

        MemberUpdateProfileRequestDto memberUpdateProfileRequestDto = createMemberUpdateProfileRequestDto(storedFile.getId(), storedFile.getFileName());

        TokenDto tokenDto = authenticationService.signIn(savedMemberId);

        //when
        ResultActions perform = mockMvc.perform(
                put("/api/members/" + savedMemberId)
                        .header(securityJwtProperties.getJwtTokenHeader(), tokenDto.getTotalAccessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(memberUpdateProfileRequestDto))
        ).andDo(print());

        //when

        //then

    }
}