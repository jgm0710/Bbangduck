package bbangduck.bd.bbangduck.domain.member.service;

import bbangduck.bd.bbangduck.domain.auth.controller.dto.MemberSocialSignUpRequestDto;
import bbangduck.bd.bbangduck.domain.file.entity.FileStorage;
import bbangduck.bd.bbangduck.domain.member.entity.*;
import bbangduck.bd.bbangduck.domain.member.entity.enbeded.RefreshInfo;
import bbangduck.bd.bbangduck.domain.member.entity.enumerate.MemberRole;
import bbangduck.bd.bbangduck.domain.member.service.dto.MemberProfileImageDto;
import bbangduck.bd.bbangduck.member.BaseJGMServiceTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("개발자 권한을 통한 회원 조작 Service 로직 테스트")
class MemberDevelopServiceTest extends BaseJGMServiceTest {

    @Autowired
    MemberDevelopService memberDevelopService;

    private final String email = "developer@bbangduck.com";
    private final String password = "bbangduckDEV7";

    @BeforeEach
    public void setUp() {
        String encodedPassword = passwordEncoder.encode(password);

        if (memberRepository.findByEmail(email).isEmpty()) {
            Member member = Member.builder()
                    .email(email)
                    .password(encodedPassword)
                    .nickname("developer")
                    .description("개발자")
                    .roomEscapeRecordsOpenYN(false)
                    .refreshInfo(RefreshInfo.init(1000))
                    .roles(Set.of(MemberRole.DEVELOP))
                    .build();

            memberRepository.save(member);
        }
    }

    @Test
    @DisplayName("개발자 권한으로 회원 삭제 테스트")
    @Transactional
    public void deleteMember() throws Exception {
        //given
        MemberSocialSignUpRequestDto memberSignUpRequestDto = createMemberSignUpRequestDto();
        Long signUpId = authenticationService.signUp(memberSignUpRequestDto.toServiceDto());
        em.flush();
        em.clear();

        MockMultipartFile files = createMockMultipartFile("files", IMAGE_FILE_CLASS_PATH);
        Long uploadImageFileId = fileStorageService.uploadImageFile(files);
        FileStorage storedFile = fileStorageService.getStoredFile(uploadImageFileId);

        MemberProfileImageDto memberProfileImageDto = new MemberProfileImageDto(storedFile.getId(), storedFile.getFileName());

        memberService.updateProfileImage(signUpId, memberProfileImageDto);
        em.flush();
        em.clear();

        Member savedMember = memberService.getMember(signUpId);
        MemberProfileImage profileImage = savedMember.getProfileImage();
        List<SocialAccount> socialAccounts = savedMember.getSocialAccounts();

        List<Long> socialAccountIdList = socialAccounts.stream().map(SocialAccount::getId).collect(Collectors.toList());

        assertNotNull(profileImage);

        //when
        memberDevelopService.deleteMember(signUpId);
        em.flush();
        em.clear();

        //then
        assertTrue(memberRepository.findById(signUpId).isEmpty());
        assertTrue(memberProfileImageRepository.findById(profileImage.getId()).isEmpty());
        socialAccountIdList.forEach(socialAccountId -> assertTrue(socialAccountRepository.findById(socialAccountId).isEmpty()));

    }
}