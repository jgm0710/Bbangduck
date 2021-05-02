package bbangduck.bd.bbangduck.domain.auth.service;

import bbangduck.bd.bbangduck.domain.member.dto.MemberSignUpDto;
import bbangduck.bd.bbangduck.domain.member.entity.Member;
import bbangduck.bd.bbangduck.domain.member.entity.SocialAccount;
import bbangduck.bd.bbangduck.domain.member.entity.SocialType;
import bbangduck.bd.bbangduck.domain.member.exception.MemberEmailDuplicateException;
import bbangduck.bd.bbangduck.domain.member.exception.MemberNicknameDuplicateException;
import bbangduck.bd.bbangduck.domain.member.service.MemberService;
import bbangduck.bd.bbangduck.global.config.properties.JwtSecurityProperties;
import bbangduck.bd.bbangduck.member.BaseMemberServiceTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AuthenticationServiceTest extends BaseMemberServiceTest {

    @Autowired
    AuthenticationService authenticationService;

    @Autowired
    JwtSecurityProperties jwtSecurityProperties;

    @Autowired
    MemberService memberService;

    @Test
    @DisplayName("회원가입")
    public void signUp() {
        //given
        MemberSignUpDto memberSignUpDto = MemberSignUpDto.builder()
                .email("test@email.com")
                .nickname("testNickname")
                .password(null)
                .socialType(SocialType.KAKAO)
                .socialId("3213123")
                .build();

        //when
        Long signMemberId = authenticationService.signUp(memberSignUpDto.signUp(jwtSecurityProperties.getRefreshTokenExpiredDate()));

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

        authenticationService.signUp(memberSignUpDto.signUp(jwtSecurityProperties.getRefreshTokenExpiredDate()));
        //when

        //then
        assertThrows(MemberEmailDuplicateException.class, () -> authenticationService.signUp(memberSignUpDto2.signUp(jwtSecurityProperties.getRefreshTokenExpiredDate())));

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

        authenticationService.signUp(memberSignUpDto.signUp(jwtSecurityProperties.getRefreshTokenExpiredDate()));
        //when

        //then
        assertThrows(MemberNicknameDuplicateException.class, () -> authenticationService.signUp(memberSignUpDto2.signUp(jwtSecurityProperties.getRefreshTokenExpiredDate())));

    }
}