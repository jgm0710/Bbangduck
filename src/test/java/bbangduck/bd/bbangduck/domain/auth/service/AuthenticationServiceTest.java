package bbangduck.bd.bbangduck.domain.auth.service;

import bbangduck.bd.bbangduck.domain.auth.controller.dto.MemberSocialSignUpRequestDto;
import bbangduck.bd.bbangduck.domain.auth.service.dto.TokenDto;
import bbangduck.bd.bbangduck.domain.auth.exception.RefreshTokenExpiredException;
import bbangduck.bd.bbangduck.domain.auth.exception.RefreshTokenNotFoundException;
import bbangduck.bd.bbangduck.domain.auth.service.dto.MemberSignUpDto;
import bbangduck.bd.bbangduck.domain.member.entity.*;
import bbangduck.bd.bbangduck.domain.member.exception.MemberDuplicateException;
import bbangduck.bd.bbangduck.member.BaseJGMServiceTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest extends BaseJGMServiceTest {

    @Test
    @DisplayName("소셜 회원가입")
    public void signUp_Social() {
        //given
        MemberSocialSignUpRequestDto memberSocialSignUpRequestDto = MemberSocialSignUpRequestDto.builder()
                .email("test@email.com")
                .nickname("testNickname")
                .socialType(SocialType.KAKAO)
                .socialId("3213123")
                .build();

        //when
        Long signMemberId = authenticationService.signUp(memberSocialSignUpRequestDto.toServiceDto());

        //then
        Member findMember = memberRepository.findById(signMemberId).orElseThrow();

        assertEquals(memberSocialSignUpRequestDto.getEmail(), findMember.getEmail());
        assertEquals(memberSocialSignUpRequestDto.getNickname(), findMember.getNickname());
        List<SocialAccount> socialAccounts = findMember.getSocialAccounts();
        SocialAccount findSocialAccount = socialAccounts.stream().filter(socialAccount -> socialAccount.getSocialId().equals(memberSocialSignUpRequestDto.getSocialId())).findFirst().orElseThrow();
        assertEquals(memberSocialSignUpRequestDto.getSocialId(), findSocialAccount.getSocialId());
        assertEquals(memberSocialSignUpRequestDto.getSocialType(), findSocialAccount.getSocialType());
    }

    @Test
    @DisplayName("일반 회원가입")
    public void signUp_Normal() {
        //given
        MemberSocialSignUpRequestDto memberSocialSignUpRequestDto = MemberSocialSignUpRequestDto.builder()
                .email("test@email.com")
                .nickname("testNickname")
                .socialType(null)
                .socialId(null)
                .build();

        //when
        Long signMemberId = authenticationService.signUp(memberSocialSignUpRequestDto.toServiceDto());

        //then
        Member findMember = memberRepository.findById(signMemberId).orElseThrow();

        assertEquals(memberSocialSignUpRequestDto.getEmail(), findMember.getEmail());
        assertEquals(memberSocialSignUpRequestDto.getNickname(), findMember.getNickname());
        List<SocialAccount> socialAccounts = findMember.getSocialAccounts();
        assertTrue(socialAccounts.isEmpty());
    }

    @Test
    @DisplayName("회원가입 이메일 중복 테스트")
    public void signUp_EmailDuplicateTest() {
        //given
        MemberSocialSignUpRequestDto memberSocialSignUpRequestDto = MemberSocialSignUpRequestDto.builder()
                .email("test@email.com")
                .nickname("testNickname")
                .socialType(SocialType.KAKAO)
                .socialId("3213123")
                .build();

        MemberSocialSignUpRequestDto memberSocialSignUpRequestDto2 = MemberSocialSignUpRequestDto.builder()
                .email("test@email.com")
                .nickname("testNickname2")
                .socialType(SocialType.KAKAO)
                .socialId("3213123")
                .build();

        authenticationService.signUp(memberSocialSignUpRequestDto.toServiceDto());
        //when

        //then
        assertThrows(MemberDuplicateException.class, () -> authenticationService.signUp(memberSocialSignUpRequestDto2.toServiceDto()));

    }

    @Test
    @DisplayName("회원가입 닉네임 중복 테스트")
    public void signUp_NicknameDuplicateTest() {
        //given
        MemberSocialSignUpRequestDto memberSocialSignUpRequestDto = MemberSocialSignUpRequestDto.builder()
                .email("test@email.com")
                .nickname("testNickname")
                .socialType(SocialType.KAKAO)
                .socialId("3213123")
                .build();

        MemberSocialSignUpRequestDto memberSocialSignUpRequestDto2 = MemberSocialSignUpRequestDto.builder()
                .email("test2@email.com")
                .nickname("testNickname")
                .socialType(SocialType.KAKAO)
                .socialId("3213123")
                .build();

        authenticationService.signUp(memberSocialSignUpRequestDto.toServiceDto());
        //when

        //then
        assertThrows(MemberDuplicateException.class, () -> authenticationService.signUp(memberSocialSignUpRequestDto2.toServiceDto()));

    }

    @Test
    @DisplayName("회원가입 소셜 정보 중복 테스트")
    public void signUp_SocialInfoDuplicate() {
        //given
        MemberSocialSignUpRequestDto memberSocialSignUpRequestDto = MemberSocialSignUpRequestDto.builder()
                .email("test@email.com")
                .nickname("testNickname")
                .socialType(SocialType.KAKAO)
                .socialId("3213123")
                .build();

        MemberSocialSignUpRequestDto memberSocialSignUpRequestDto2 = MemberSocialSignUpRequestDto.builder()
                .email("test2@email.com")
                .nickname("testNickname2")
                .socialType(SocialType.KAKAO)
                .socialId("3213123")
                .build();

        authenticationService.signUp(memberSocialSignUpRequestDto.toServiceDto());
        //when

        //then
        assertThrows(MemberDuplicateException.class, () -> authenticationService.signUp(memberSocialSignUpRequestDto2.toServiceDto()));

    }

    @Test
    @DisplayName("Refresh 테스트")
    public void refresh() {
        //given
        MemberSocialSignUpRequestDto memberSocialSignUpRequestDto = MemberSocialSignUpRequestDto.builder()
                .email("test@email.com")
                .nickname("testNickname")
                .socialType(SocialType.KAKAO)
                .socialId("3213123")
                .build();

        Long savedMemberId = authenticationService.signUp(memberSocialSignUpRequestDto.toServiceDto());
        TokenDto tokenDto = authenticationService.signIn(savedMemberId);

        //when
        TokenDto refreshedTokenDto = authenticationService.refresh(tokenDto.getRefreshToken());

        //then
        System.out.println("refreshedTokenDto = " + refreshedTokenDto);
        assertEquals(tokenDto.getRefreshToken(), refreshedTokenDto.getRefreshToken());
        assertEquals(tokenDto.getRefreshTokenExpiredDate(), refreshedTokenDto.getRefreshTokenExpiredDate());
        assertEquals(tokenDto.getAccessTokenValidSecond(), refreshedTokenDto.getAccessTokenValidSecond());
    }

    @Test
    @DisplayName("Refresh Token 을 통한 회원 조회가 불가능할 경우")
    public void refresh_NotFound() {
        //given

        //when

        //then
        assertThrows(RefreshTokenNotFoundException.class, () -> authenticationService.refresh("jfioewnfkldsnfklds"));

    }

    @Test
    @DisplayName("Refresh Token 의 유효기간이 만료됐을 경우")
    public void refresh_Expired(@Mock MemberSignUpDto memberSignUpDto) {
        //given
        RefreshInfo refreshInfo = RefreshInfo.builder()
                .refreshToken(UUID.randomUUID().toString())
                .refreshTokenExpiredDate(LocalDateTime.now().minusDays(1))
                .build();

        Member member = Member.builder()
                .email("test@email.com")
                .nickname("test")
                .password("test")
                .refreshInfo(refreshInfo)
                .roles(Set.of(MemberRole.USER))
                .build();

        memberRepository.save(member);
        em.flush();
        em.clear();

        //when

        //then
        assertThrows(RefreshTokenExpiredException.class, () -> authenticationService.refresh(refreshInfo.getRefreshToken()));

    }

    @Test
    @DisplayName("로그인 Refresh Token 갱신 테스트")
    public void signIn_refreshToken_refresh(@Mock MemberSignUpDto memberSignUpDto) {
        //given
        RefreshInfo refreshInfo = RefreshInfo.builder()
                .refreshToken(UUID.randomUUID().toString())
                .refreshTokenExpiredDate(LocalDateTime.now().minusDays(1))
                .build();

        Member member = Member.builder()
                .email("test@email.com")
                .nickname("test")
                .password("test")
                .refreshInfo(refreshInfo)
                .roles(Set.of(MemberRole.USER))
                .build();

        Member saved = memberRepository.save(member);
        Long savedMemberId = saved.getId();

        Member savedMember = memberService.getMember(savedMemberId);

        String refreshToken = savedMember.getRefreshToken();
        LocalDateTime refreshTokenExpiredDate = savedMember.getRefreshTokenExpiredDate();

        //when
        TokenDto tokenDto = authenticationService.signIn(savedMemberId);
        em.flush();
        em.clear();

        //then
        Member findMember = memberService.getMember(savedMemberId);

        assertNotEquals(refreshToken, findMember.getRefreshToken());
        assertNotEquals(refreshTokenExpiredDate, findMember.getRefreshTokenExpiredDate());
        assertEquals(tokenDto.getRefreshToken(), findMember.getRefreshToken());

    }


}