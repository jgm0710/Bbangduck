package bbangduck.bd.bbangduck.member;

import bbangduck.bd.bbangduck.domain.file.entity.FileStorage;
import bbangduck.bd.bbangduck.domain.file.exception.StoredFileNotFoundException;
import bbangduck.bd.bbangduck.domain.member.controller.dto.MemberUpdateProfileRequestDto;
import bbangduck.bd.bbangduck.domain.auth.controller.dto.MemberSocialSignUpRequestDto;
import bbangduck.bd.bbangduck.domain.member.entity.Member;
import bbangduck.bd.bbangduck.domain.member.entity.MemberProfileImage;
import bbangduck.bd.bbangduck.domain.member.entity.SocialType;
import bbangduck.bd.bbangduck.domain.member.exception.MemberNicknameDuplicateException;
import bbangduck.bd.bbangduck.domain.member.exception.MemberNotFoundException;
import bbangduck.bd.bbangduck.domain.member.service.MemberService;
import bbangduck.bd.bbangduck.domain.member.service.dto.MemberProfileImageDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import static org.junit.jupiter.api.Assertions.*;

class MemberServiceTest extends BaseJGMServiceTest {

    @Autowired
    MemberService memberService;

    @Autowired
    EntityManager entityManager;

    @Test
    @DisplayName("회원 조회 테스트")
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

        em.flush();
        em.clear();
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

}