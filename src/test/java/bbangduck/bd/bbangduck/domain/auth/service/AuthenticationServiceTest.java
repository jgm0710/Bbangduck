package bbangduck.bd.bbangduck.domain.auth.service;

import bbangduck.bd.bbangduck.domain.member.dto.MemberSignUpDto;
import bbangduck.bd.bbangduck.domain.member.entity.Member;
import bbangduck.bd.bbangduck.domain.member.entity.SocialAccount;
import bbangduck.bd.bbangduck.domain.member.entity.SocialType;
import bbangduck.bd.bbangduck.domain.member.exception.MemberDuplicateException;
import bbangduck.bd.bbangduck.domain.member.service.MemberService;
import bbangduck.bd.bbangduck.global.config.properties.SecurityJwtProperties;
import bbangduck.bd.bbangduck.member.BaseJGMServiceTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AuthenticationServiceTest extends BaseJGMServiceTest {

    @Test
    @DisplayName("소셜 회원가입")
    public void signUp_Social() {
        //given
        MemberSignUpDto memberSignUpDto = MemberSignUpDto.builder()
                .email("test@email.com")
                .nickname("testNickname")
                .password(null)
                .socialType(SocialType.KAKAO)
                .socialId("3213123")
                .build();

        //when
        Long signMemberId = authenticationService.signUp(memberSignUpDto.signUp(securityJwtProperties.getRefreshTokenExpiredDate()));

        //then
        Member findMember = memberRepository.findById(signMemberId).orElseThrow();

        assertEquals(memberSignUpDto.getEmail(), findMember.getEmail());
        assertEquals(memberSignUpDto.getNickname(), findMember.getNickname());
        assertEquals(memberSignUpDto.getPassword(), findMember.getPassword());
        List<SocialAccount> socialAccounts = findMember.getSocialAccounts();
        SocialAccount findSocialAccount = socialAccounts.stream().filter(socialAccount -> socialAccount.getSocialId().equals(memberSignUpDto.getSocialId())).findFirst().orElseThrow();
        assertEquals(memberSignUpDto.getSocialId(), findSocialAccount.getSocialId());
        assertEquals(memberSignUpDto.getSocialType(), findSocialAccount.getSocialType());
    }

    @Test
    @DisplayName("일반 회원가입")
    public void signUp_Normal() {
        //given
        MemberSignUpDto memberSignUpDto = MemberSignUpDto.builder()
                .email("test@email.com")
                .nickname("testNickname")
                .password("test")
                .socialType(null)
                .socialId(null)
                .build();

        //when
        Long signMemberId = authenticationService.signUp(memberSignUpDto.signUp(securityJwtProperties.getRefreshTokenExpiredDate()));

        //then
        Member findMember = memberRepository.findById(signMemberId).orElseThrow();

        assertEquals(memberSignUpDto.getEmail(), findMember.getEmail());
        assertEquals(memberSignUpDto.getNickname(), findMember.getNickname());
        assertEquals(memberSignUpDto.getPassword(), findMember.getPassword());
        List<SocialAccount> socialAccounts = findMember.getSocialAccounts();
        assertTrue(socialAccounts.isEmpty());
    }

    @Test
    @DisplayName("회원가입 이메일 중복 테스트")
    public void signUp_EmailDuplicateTest() {
        //given
        MemberSignUpDto memberSignUpDto = MemberSignUpDto.builder()
                .email("test@email.com")
                .nickname("testNickname")
                .password(null)
                .socialType(SocialType.KAKAO)
                .socialId("3213123")
                .build();

        MemberSignUpDto memberSignUpDto2 = MemberSignUpDto.builder()
                .email("test@email.com")
                .nickname("testNickname2")
                .password(null)
                .socialType(SocialType.KAKAO)
                .socialId("3213123")
                .build();

        authenticationService.signUp(memberSignUpDto.signUp(securityJwtProperties.getRefreshTokenExpiredDate()));
        //when

        //then
        assertThrows(MemberDuplicateException.class, () -> authenticationService.signUp(memberSignUpDto2.signUp(securityJwtProperties.getRefreshTokenExpiredDate())));

    }

    @Test
    @DisplayName("회원가입 닉네임 중복 테스트")
    public void signUp_NicknameDuplicateTest() {
        //given
        MemberSignUpDto memberSignUpDto = MemberSignUpDto.builder()
                .email("test@email.com")
                .nickname("testNickname")
                .password(null)
                .socialType(SocialType.KAKAO)
                .socialId("3213123")
                .build();

        MemberSignUpDto memberSignUpDto2 = MemberSignUpDto.builder()
                .email("test2@email.com")
                .nickname("testNickname")
                .password(null)
                .socialType(SocialType.KAKAO)
                .socialId("3213123")
                .build();

        authenticationService.signUp(memberSignUpDto.signUp(securityJwtProperties.getRefreshTokenExpiredDate()));
        //when

        //then
        assertThrows(MemberDuplicateException.class, () -> authenticationService.signUp(memberSignUpDto2.signUp(securityJwtProperties.getRefreshTokenExpiredDate())));

    }

    @Test
    @DisplayName("회원가입 소셜 정보 중복 테스트")
    public void signUp_SocialInfoDuplicate() {
        //given
        MemberSignUpDto memberSignUpDto = MemberSignUpDto.builder()
                .email("test@email.com")
                .nickname("testNickname")
                .password(null)
                .socialType(SocialType.KAKAO)
                .socialId("3213123")
                .build();

        MemberSignUpDto memberSignUpDto2 = MemberSignUpDto.builder()
                .email("test2@email.com")
                .nickname("testNickname2")
                .password(null)
                .socialType(SocialType.KAKAO)
                .socialId("3213123")
                .build();

        authenticationService.signUp(memberSignUpDto.signUp(securityJwtProperties.getRefreshTokenExpiredDate()));
        //when

        //then
        assertThrows(MemberDuplicateException.class, () -> authenticationService.signUp(memberSignUpDto2.signUp(securityJwtProperties.getRefreshTokenExpiredDate())));

    }
}