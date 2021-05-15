package bbangduck.bd.bbangduck.member;

import bbangduck.bd.bbangduck.domain.file.entity.FileStorage;
import bbangduck.bd.bbangduck.domain.member.controller.dto.MemberUpdateProfileRequestDto;
import bbangduck.bd.bbangduck.domain.auth.controller.dto.MemberSocialSignUpRequestDto;
import bbangduck.bd.bbangduck.domain.member.entity.Member;
import bbangduck.bd.bbangduck.domain.member.entity.MemberProfileImage;
import bbangduck.bd.bbangduck.domain.member.exception.MemberNotFoundException;
import bbangduck.bd.bbangduck.domain.member.service.MemberService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
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
    @DisplayName("회원 프로필 수정 테스트")
    public void updateMember() throws Exception {
        //given
        MemberSocialSignUpRequestDto signUpDto = createMemberSignUpDto();

        Long savedMemberId = authenticationService.signUp(signUpDto.toServiceDto());

        MockMultipartFile multipartFile = createMockMultipartFile("files", IMAGE_FILE_CLASS_PATH);
        Long storedFileId = fileStorageService.uploadImageFile(multipartFile);
        FileStorage storedFile = fileStorageService.getStoredFile(storedFileId);

        Long fileId = storedFile.getId();
        String fileName = storedFile.getFileName();
        MemberUpdateProfileRequestDto updateDto = createMemberModifyDto(fileId, fileName);

        em.flush();

        //when
        memberService.updateMember(savedMemberId, updateDto.toServiceDto());
        em.flush();
        em.clear();

        //then
        Member modifiedMember = memberService.getMember(savedMemberId);
        assertEquals(updateDto.getNickname(), modifiedMember.getNickname());
        assertEquals(updateDto.getDescription(), modifiedMember.getDescription());

        MemberProfileImage profileImage = modifiedMember.getProfileImage();
        assertEquals(updateDto.getProfileImageId(), profileImage.getFileId());
        assertEquals(updateDto.getProfileImageName(), profileImage.getFileName());
    }

    @Test
    @DisplayName("회원 프로필 수정 시 회원을 찾을 수 없는 경우")
    public void updateMember_NotFound() throws Exception {
        //given
        MockMultipartFile multipartFile = createMockMultipartFile("files", IMAGE_FILE_CLASS_PATH);
        Long storedFileId = fileStorageService.uploadImageFile(multipartFile);
        FileStorage storedFile = fileStorageService.getStoredFile(storedFileId);

        Long fileId = storedFile.getId();
        String fileName = storedFile.getFileName();
        MemberUpdateProfileRequestDto modifyDto = createMemberModifyDto(fileId, fileName);

        //when

        //then
        assertThrows(MemberNotFoundException.class, () -> memberService.updateMember(100000L, modifyDto.toServiceDto()));

    }

}