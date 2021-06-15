package bbangduck.bd.bbangduck.member;

import bbangduck.bd.bbangduck.domain.auth.dto.controller.MemberSocialSignUpRequestDto;
import bbangduck.bd.bbangduck.domain.file.entity.FileStorage;
import bbangduck.bd.bbangduck.domain.file.exception.StoredFileNotFoundException;
import bbangduck.bd.bbangduck.domain.member.entity.Member;
import bbangduck.bd.bbangduck.domain.member.entity.MemberProfileImage;
import bbangduck.bd.bbangduck.domain.member.exception.MemberNicknameDuplicateException;
import bbangduck.bd.bbangduck.domain.member.exception.MemberNotFoundException;
import bbangduck.bd.bbangduck.domain.member.exception.MemberProfileImageNotFoundException;
import bbangduck.bd.bbangduck.domain.member.service.MemberService;
import bbangduck.bd.bbangduck.domain.member.dto.service.MemberProfileImageDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("회원(프로필 정보) 관련 Service 로직 테스트")
class MemberServiceTest extends BaseJGMServiceTest {

    @Autowired
    MemberService memberService;

    @Autowired
    EntityManager entityManager;

    @Test
    @DisplayName("회원 조회 테스트")
    @Transactional
    public void getMemberTest() {
        //given
        Member member = Member.builder()
                .email("test@email.com")
                .nickname("testNickname")
                .build();

        Member save = memberRepository.save(member);

        entityManager.flush();
        entityManager.clear();

        //when
        Member findMember = memberService.getMember(save.getId());
        System.out.println("findMember = " + findMember.toString());

        //then
        assertEquals(findMember.getId(), member.getId());
        assertEquals(findMember.getEmail(), member.getEmail());
        assertEquals(findMember.getNickname(), member.getNickname());
    }

    @Test
    @DisplayName("회원 조회 시 회원을 찾을 수 없는 경우")
    public void getMember_NotFound() {
        //given

        //when

        //then
        assertThrows(MemberNotFoundException.class, () -> memberService.getMember(10000L));
    }

    @Test
    @DisplayName("회원 프로필 이미지 변경 - 기존에 프로필 이미지가 없었을 경우")
    public void updateProfileImage_ProfileImageNotExist() throws Exception {
        //given
        MemberSocialSignUpRequestDto memberSignUpRequestDto = createMemberSignUpRequestDto();
        Long signUpId = authenticationService.signUp(memberSignUpRequestDto.toServiceDto());
        Member savedMember = memberService.getMember(signUpId);
        assertNull(savedMember.getProfileImage(), "처음 회원가입 한 회원의 프로필 이미지는 null 이어야 한다.");

        MockMultipartFile files = createMockMultipartFile("files", IMAGE_FILE_CLASS_PATH);
        Long uploadedFileId = fileStorageService.uploadImageFile(files);
        FileStorage storedFile = fileStorageService.getStoredFile(uploadedFileId);

        MemberProfileImageDto memberProfileImageDto = MemberProfileImageDto.builder()
                .fileStorageId(storedFile.getId())
                .fileName(storedFile.getFileName())
                .build();

        //when
        memberService.updateProfileImage(signUpId, memberProfileImageDto);

        //then
        Member findMember = memberService.getMember(signUpId);

        assertNotNull(findMember.getProfileImage(), "프로필 이미지 변경 이후 프로필 이미지는 null 이 아니어야 한다.");
        MemberProfileImage memberProfileImage = findMember.getProfileImage();
        assertEquals(storedFile.getId(), memberProfileImage.getFileStorageId());
        assertEquals(storedFile.getFileName(), memberProfileImage.getFileName());

    }

    @Test
    @DisplayName("회원 프로필 이미지 변경 - 기존 프로필 이미지가 있었을 경우")
    @Transactional
    public void updateProfileImage_ProfileImageExist() throws Exception {
        //given
        MemberSocialSignUpRequestDto memberSignUpRequestDto = createMemberSignUpRequestDto();
        Long signUpId = authenticationService.signUp(memberSignUpRequestDto.toServiceDto());
        Member savedMember = memberService.getMember(signUpId);
        assertNull(savedMember.getProfileImage(), "처음 회원가입 한 회원의 프로필 이미지는 null 이어야 한다.");

        MockMultipartFile files = createMockMultipartFile("files", IMAGE_FILE_CLASS_PATH);
        Long uploadedFileId = fileStorageService.uploadImageFile(files);
        FileStorage storedFile = fileStorageService.getStoredFile(uploadedFileId);

        MemberProfileImageDto memberProfileImageDto = MemberProfileImageDto.builder()
                .fileStorageId(storedFile.getId())
                .fileName(storedFile.getFileName())
                .build();

        //when
        memberService.updateProfileImage(signUpId, memberProfileImageDto);

        em.flush();
        em.clear();
        //then
        Member findMember = memberService.getMember(signUpId);

        assertNotNull(findMember.getProfileImage(), "프로필 이미지 변경 이후 프로필 이미지는 null 이 아니어야 한다.");
        MemberProfileImage memberProfileImage = findMember.getProfileImage();
        assertEquals(storedFile.getId(), memberProfileImage.getFileStorageId());
        assertEquals(storedFile.getFileName(), memberProfileImage.getFileName());

        em.flush();
        em.clear();

        //given
        MockMultipartFile files2 = createMockMultipartFile("files", IMAGE_FILE2_CLASS_PATH);
        Long uploadedFileId2 = fileStorageService.uploadImageFile(files2);
        FileStorage storedFile2 = fileStorageService.getStoredFile(uploadedFileId2);

        assertNotEquals(storedFile.getId(), storedFile2.getId());

        MemberProfileImageDto memberProfileImageDto2 = new MemberProfileImageDto(storedFile2.getId(), storedFile2.getFileName());

        //when
        memberService.updateProfileImage(signUpId, memberProfileImageDto2);
        em.flush();
        em.clear();

        //then
        assertThrows(StoredFileNotFoundException.class, () -> fileStorageService.getStoredFile(storedFile.getId()));

        Member findMember2 = memberService.getMember(signUpId);
        MemberProfileImage memberProfileImage2 = findMember2.getProfileImage();

        assertEquals(memberProfileImage.getId(), memberProfileImage2.getId());
        assertNotEquals(memberProfileImage.getFileStorageId(), memberProfileImage2.getFileStorageId());
        assertNotEquals(memberProfileImage.getFileName(), memberProfileImage2.getFileName());

        assertEquals(storedFile2.getId(), memberProfileImage2.getFileStorageId());
        assertEquals(storedFile2.getFileName(), memberProfileImage2.getFileName());

    }

    @Test
    @DisplayName("프로필 이미지 수정 시 회원을 찾을 수 없는 경우")
    public void updateProfileImage_MemberNotFound() throws Exception {
        //given
        MockMultipartFile files = createMockMultipartFile("files", IMAGE_FILE_CLASS_PATH);
        Long uploadedFileId = fileStorageService.uploadImageFile(files);
        FileStorage storedFile = fileStorageService.getStoredFile(uploadedFileId);

        MemberProfileImageDto memberProfileImageDto = new MemberProfileImageDto(storedFile.getId(), storedFile.getFileName());

        //when

        //then
        assertThrows(MemberNotFoundException.class, () -> memberService.updateProfileImage(10000L, memberProfileImageDto));

    }

    @Test
    @DisplayName("회원 프로필 이미지 삭제")
    public void deleteProfileImage() throws Exception {
        //given
        MemberSocialSignUpRequestDto memberSignUpRequestDto = createMemberSignUpRequestDto();
        Long signUpId = authenticationService.signUp(memberSignUpRequestDto.toServiceDto());

        MockMultipartFile files = createMockMultipartFile("files", IMAGE_FILE_CLASS_PATH);
        Long uploadId = fileStorageService.uploadImageFile(files);
        FileStorage storedFile = fileStorageService.getStoredFile(uploadId);

        MemberProfileImageDto memberProfileImageDto = new MemberProfileImageDto(storedFile.getId(), storedFile.getFileName());

        memberService.updateProfileImage(signUpId, memberProfileImageDto);

        Member savedMember = memberService.getMember(signUpId);
        MemberProfileImage profileImage = savedMember.getProfileImage();
        assertNotNull(profileImage);
        assertEquals(storedFile.getId(), profileImage.getFileStorageId());
        assertEquals(storedFile.getFileName(), profileImage.getFileName());

        Long profileImageId = profileImage.getId();

        //when
        memberService.deleteProfileImage(signUpId);

        //then
        Member findMember = memberService.getMember(signUpId);

        assertNull(findMember.getProfileImage(), "프로필 이미지 삭제 이후 회원의 프로필 이미지는 null 이어야 한다.");
        assertTrue(memberProfileImageRepository.findById(profileImageId).isEmpty());


    }

    @Test
    @DisplayName("회원 프로필 이미지 삭제 - 회원을 찾을 수 없는 경우")
    public void deleteProfileImage_MemberNotFound() throws Exception {
        //given
        MemberSocialSignUpRequestDto memberSignUpRequestDto = createMemberSignUpRequestDto();
        Long signUpId = authenticationService.signUp(memberSignUpRequestDto.toServiceDto());

        MockMultipartFile files = createMockMultipartFile("files", IMAGE_FILE_CLASS_PATH);
        Long uploadId = fileStorageService.uploadImageFile(files);
        FileStorage storedFile = fileStorageService.getStoredFile(uploadId);

        MemberProfileImageDto memberProfileImageDto = new MemberProfileImageDto(storedFile.getId(), storedFile.getFileName());

        memberService.updateProfileImage(signUpId, memberProfileImageDto);

        Member savedMember = memberService.getMember(signUpId);
        MemberProfileImage profileImage = savedMember.getProfileImage();
        assertNotNull(profileImage);
        assertEquals(storedFile.getId(), profileImage.getFileStorageId());
        assertEquals(storedFile.getFileName(), profileImage.getFileName());

        Long profileImageId = profileImage.getId();

        //when

        //then
        assertThrows(MemberNotFoundException.class, () -> memberService.deleteProfileImage(10000L));


    }

    @Test
    @DisplayName("회원 프로필 이미지 삭제 - 회원 프로필 이미지를 찾을 수 없는 경우")
    public void deleteProfileImage_MemberProfileImageNotFound() throws Exception {
        //given
        MemberSocialSignUpRequestDto memberSignUpRequestDto = createMemberSignUpRequestDto();
        Long signUpId = authenticationService.signUp(memberSignUpRequestDto.toServiceDto());

        MockMultipartFile files = createMockMultipartFile("files", IMAGE_FILE_CLASS_PATH);
        Long uploadId = fileStorageService.uploadImageFile(files);
        FileStorage storedFile = fileStorageService.getStoredFile(uploadId);

        MemberProfileImageDto memberProfileImageDto = new MemberProfileImageDto(storedFile.getId(), storedFile.getFileName());

        memberService.updateProfileImage(signUpId, memberProfileImageDto);

        Member savedMember = memberService.getMember(signUpId);
        MemberProfileImage profileImage = savedMember.getProfileImage();
        assertNotNull(profileImage);
        assertEquals(storedFile.getId(), profileImage.getFileStorageId());
        assertEquals(storedFile.getFileName(), profileImage.getFileName());

        Long profileImageId = profileImage.getId();

        //when
        memberService.deleteProfileImage(signUpId);

        //then
        assertThrows(MemberProfileImageNotFoundException.class, () -> memberService.deleteProfileImage(signUpId));

    }

    @Test
    @DisplayName("회원 닉네임 변경")
    public void updateNickname() throws Exception {
        //given
        MemberSocialSignUpRequestDto memberSignUpRequestDto = createMemberSignUpRequestDto();
        Long signUpId = authenticationService.signUp(memberSignUpRequestDto.toServiceDto());

        //when
        String newNickname = "updateNewNickname";
        memberService.updateNickname(signUpId, newNickname);

        //then
        Member findMember = memberService.getMember(signUpId);
        assertEquals(newNickname, findMember.getNickname());

    }

    @Test
    @DisplayName("회원 닉네임 변경 - 회원을 찾을 수 없는 경우")
    public void updateNickname_MemberNotFound() throws Exception {
        //given
        MemberSocialSignUpRequestDto memberSignUpRequestDto = createMemberSignUpRequestDto();
        Long signUpId = authenticationService.signUp(memberSignUpRequestDto.toServiceDto());

        String newNickname = "updateNewNickname";
        //when

        //then
        assertThrows(MemberNotFoundException.class, () -> memberService.updateNickname(10000L, newNickname));

    }

    @Test
    @DisplayName("회원 닉네임 변경 - 닉네임을 변경하지 않은 경우")
    public void updateNickname_NicknameNotUpdated() throws Exception {
        //given
        MemberSocialSignUpRequestDto memberSignUpRequestDto = createMemberSignUpRequestDto();
        Long signUpId1 = authenticationService.signUp(memberSignUpRequestDto.toServiceDto());

        String newNickname = "test2";
        memberSignUpRequestDto.setEmail("test2@email.com");
        memberSignUpRequestDto.setNickname(newNickname);
        memberSignUpRequestDto.setSocialId("3322113");
        Long signUpId2 = authenticationService.signUp(memberSignUpRequestDto.toServiceDto());

        //when
        memberService.updateNickname(signUpId2, newNickname);

        //then
        Member findMember = memberService.getMember(signUpId2);
        assertEquals(newNickname, findMember.getNickname());

    }

    @Test
    @DisplayName("회원 닉네임 변경 - 다른 회원의 닉네임과 중복되는 경우")
    public void updateNickname_NicknameDuplicate() throws Exception {
        //given
        MemberSocialSignUpRequestDto memberSignUpRequestDto = createMemberSignUpRequestDto();
        Long signUpId1 = authenticationService.signUp(memberSignUpRequestDto.toServiceDto());

        String newNickname = "test2";
        memberSignUpRequestDto.setEmail("test2@email.com");
        memberSignUpRequestDto.setNickname(newNickname);
        memberSignUpRequestDto.setSocialId("3322113");
        Long signUpId2 = authenticationService.signUp(memberSignUpRequestDto.toServiceDto());

        //when

        //then
        assertThrows(MemberNicknameDuplicateException.class, () -> memberService.updateNickname(signUpId1, newNickname));

    }

    @Test
    @DisplayName("회원 자기소개 변경")
    public void updateDescription() throws Exception {
        //given
        MemberSocialSignUpRequestDto memberSignUpRequestDto = createMemberSignUpRequestDto();
        Long signUpId = authenticationService.signUp(memberSignUpRequestDto.toServiceDto());
        Member savedMember = memberService.getMember(signUpId);
        assertNull(savedMember.getDescription(), "회원가입 직후 회원의 자기소개는 null 이어야 한다.");

        String newDescription = "새로운 자기 소개";

        //when
        memberService.updateDescription(signUpId, newDescription);

        //then
        Member findMember = memberService.getMember(signUpId);
        assertEquals(newDescription, findMember.getDescription());

    }

    @Test
    @DisplayName("회원 자기소개 변경 - 회원을 찾을 수 없는 경우")
    public void updateDescription_MemberNotFound() throws Exception {
        //given
        MemberSocialSignUpRequestDto memberSignUpRequestDto = createMemberSignUpRequestDto();
        Long signUpId = authenticationService.signUp(memberSignUpRequestDto.toServiceDto());
        Member savedMember = memberService.getMember(signUpId);
        assertNull(savedMember.getDescription(), "회원가입 직후 회원의 자기소개는 null 이어야 한다.");

        String newDescription = "새로운 자기 소개";

        //when

        //then
        assertThrows(MemberNotFoundException.class, () -> memberService.updateDescription(10000L, newDescription));

    }

    @Test
    @DisplayName("회원 방탈출 기록 공개 여부 변경 - true to false")
    public void toggleRoomEscapeRecodesOpenYN_TrueToFalse() throws Exception {
        //given
        MemberSocialSignUpRequestDto memberSignUpRequestDto = createMemberSignUpRequestDto();
        Long signUpId = authenticationService.signUp(memberSignUpRequestDto.toServiceDto());
        Member savedMember = memberService.getMember(signUpId);
        assertTrue(savedMember.isRoomEscapeRecordsOpenYN());

        //when
        memberService.toggleRoomEscapeRecodesOpenYN(signUpId);

        //then
        Member findMember = memberService.getMember(signUpId);
        assertFalse(findMember.isRoomEscapeRecordsOpenYN());

    }

    @Test
    @DisplayName("회원 방탈출 기록 공개 여부 변경 - false to true")
    public void toggleRoomEscapeRecodesOpenYN_FalseToTrue() throws Exception {
        //given
        MemberSocialSignUpRequestDto memberSignUpRequestDto = createMemberSignUpRequestDto();
        Long signUpId = authenticationService.signUp(memberSignUpRequestDto.toServiceDto());

        memberService.toggleRoomEscapeRecodesOpenYN(signUpId);

        Member savedMember = memberService.getMember(signUpId);
        assertFalse(savedMember.isRoomEscapeRecordsOpenYN());

        //when
        memberService.toggleRoomEscapeRecodesOpenYN(signUpId);

        //then
        Member findMember = memberService.getMember(signUpId);
        assertTrue(findMember.isRoomEscapeRecordsOpenYN());


    }

    @Test
    @DisplayName("회원 방탈출 기록 공개 여부 변경 - 회원을 찾을 수 없는 경우")
    public void toggleRoomEscapeRecodesOpenYN_MemberNotFound() throws Exception {
        //given
        MemberSocialSignUpRequestDto memberSignUpRequestDto = createMemberSignUpRequestDto();
        Long signUpId = authenticationService.signUp(memberSignUpRequestDto.toServiceDto());
        Member savedMember = memberService.getMember(signUpId);
        assertTrue(savedMember.isRoomEscapeRecordsOpenYN());

        //when

        //then
        assertThrows(MemberNotFoundException.class, () -> memberService.toggleRoomEscapeRecodesOpenYN(10000L));

    }

}