package bbangduck.bd.bbangduck.member;

import bbangduck.bd.bbangduck.domain.file.entity.FileStorage;
import bbangduck.bd.bbangduck.domain.member.controller.dto.MemberUpdateProfileRequestDto;
import bbangduck.bd.bbangduck.domain.auth.controller.dto.MemberSocialSignUpRequestDto;
import bbangduck.bd.bbangduck.domain.member.entity.Member;
import bbangduck.bd.bbangduck.domain.member.entity.MemberProfileImage;
import bbangduck.bd.bbangduck.domain.member.entity.SocialType;
import bbangduck.bd.bbangduck.domain.member.exception.MemberNicknameDuplicateException;
import bbangduck.bd.bbangduck.domain.member.exception.MemberNotFoundException;
import bbangduck.bd.bbangduck.domain.member.service.MemberService;
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
    @DisplayName("회원 프로필 수정 테스트")
    public void updateMember() throws Exception {
        //given
        MemberSocialSignUpRequestDto signUpDto = createMemberSignUpRequestDto();

        Long savedMemberId = authenticationService.signUp(signUpDto.toServiceDto());

        MockMultipartFile multipartFile = createMockMultipartFile("files", IMAGE_FILE_CLASS_PATH);
        Long storedFileId = fileStorageService.uploadImageFile(multipartFile);
        FileStorage storedFile = fileStorageService.getStoredFile(storedFileId);

        Long fileId = storedFile.getId();
        String fileName = storedFile.getFileName();
        MemberUpdateProfileRequestDto updateDto = createMemberUpdateRequestDto(fileId, fileName);

        em.flush();

        Member savedMember = memberService.getMember(savedMemberId);
        assertTrue(savedMember.isRoomEscapeRecordsOpenYN(), "회원가입 기본 방탈출 기록 공개는 true");

        //when
        memberService.updateMember(savedMemberId, updateDto.toServiceDto());
        em.flush();
        em.clear();

        //then
        Member modifiedMember = memberService.getMember(savedMemberId);
        assertEquals(updateDto.getNickname(), modifiedMember.getNickname());
        assertEquals(updateDto.getDescription(), modifiedMember.getDescription());

        MemberProfileImage profileImage = modifiedMember.getProfileImage();
        assertEquals(updateDto.getFileStorageId(), profileImage.getFileStorageId());
        assertEquals(updateDto.getFileName(), profileImage.getFileName());
        assertEquals(updateDto.isRoomEscapeRecordsOpenYN(), modifiedMember.isRoomEscapeRecordsOpenYN());

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
        MemberUpdateProfileRequestDto modifyDto = createMemberUpdateRequestDto(fileId, fileName);

        //when

        //then
        assertThrows(MemberNotFoundException.class, () -> memberService.updateMember(100000L, modifyDto.toServiceDto()));

    }

    @Test
    @DisplayName("회원 프로필 수정 시 다른 회원의 닉네임과 중복되는 경우")
    public void updateMember_NicknameDuplicate() {
        //given
        MemberSocialSignUpRequestDto memberSignUpDto = createMemberSignUpRequestDto();
        Long signUpMemberId = authenticationService.signUp(memberSignUpDto.toServiceDto());

        String nickname = "홍길동2";
        memberSignUpDto.setNickname(nickname);
        memberSignUpDto.setEmail("test2@email.com");
        memberSignUpDto.setSocialId("3123123");
        memberSignUpDto.setSocialType(SocialType.KAKAO);
        authenticationService.signUp(memberSignUpDto.toServiceDto());


        MemberUpdateProfileRequestDto memberUpdateRequestDto = createMemberUpdateRequestDto(null,null);
        memberUpdateRequestDto.setNickname(nickname);

        //when

        //then
        assertThrows(MemberNicknameDuplicateException.class, () -> memberService.updateMember(signUpMemberId, memberUpdateRequestDto.toServiceDto()));

    }

    @Test
    @DisplayName("회원 프로필 수정 시 닉네임을 변경하지 않은 경우")
    public void updateMember_NicknameNotUpdate() {
        //given
        MemberSocialSignUpRequestDto memberSignUpDto = createMemberSignUpRequestDto();
        Long savedMemberId1 = authenticationService.signUp(memberSignUpDto.toServiceDto());

        String nickname = "홍길동2";
        memberSignUpDto.setNickname(nickname);
        memberSignUpDto.setEmail("test2@email.com");
        memberSignUpDto.setSocialId("3123123");
        memberSignUpDto.setSocialType(SocialType.KAKAO);
        Long savedMemberId2 = authenticationService.signUp(memberSignUpDto.toServiceDto());


        MemberUpdateProfileRequestDto memberUpdateRequestDto = createMemberUpdateRequestDto(null,null);
        memberUpdateRequestDto.setNickname(nickname);

        //when
        memberService.updateMember(savedMemberId2, memberUpdateRequestDto.toServiceDto());

        //then
        Member findMember = memberService.getMember(savedMemberId2);
        assertEquals(memberUpdateRequestDto.getNickname(), findMember.getNickname());
        assertEquals(memberUpdateRequestDto.getDescription(), findMember.getDescription());

    }

}