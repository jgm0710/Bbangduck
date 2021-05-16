package bbangduck.bd.bbangduck.domain.auth.service;

import bbangduck.bd.bbangduck.domain.auth.controller.dto.MemberSocialSignUpRequestDto;
import bbangduck.bd.bbangduck.domain.auth.exception.RefreshTokenExpiredException;
import bbangduck.bd.bbangduck.domain.auth.exception.RefreshTokenNotFoundException;
import bbangduck.bd.bbangduck.domain.auth.service.dto.MemberSignUpDto;
import bbangduck.bd.bbangduck.domain.auth.service.dto.TokenDto;
import bbangduck.bd.bbangduck.domain.member.entity.*;
import bbangduck.bd.bbangduck.domain.member.exception.MemberEmailDuplicateException;
import bbangduck.bd.bbangduck.domain.member.exception.MemberNicknameDuplicateException;
import bbangduck.bd.bbangduck.domain.member.exception.MemberSocialInfoDuplicateException;
import bbangduck.bd.bbangduck.member.BaseJGMServiceTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

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
        Member findMember = memberService.getMember(signMemberId);

        assertEquals(memberSocialSignUpRequestDto.getEmail(), findMember.getEmail());
        assertEquals(memberSocialSignUpRequestDto.getNickname(), findMember.getNickname());
        List<SocialAccount> socialAccounts = findMember.getSocialAccounts();
        SocialAccount findSocialAccount = socialAccounts.stream().filter(socialAccount -> socialAccount.getSocialId().equals(memberSocialSignUpRequestDto.getSocialId())).findFirst().orElseThrow();
        assertEquals(memberSocialSignUpRequestDto.getSocialId(), findSocialAccount.getSocialId());
        assertEquals(memberSocialSignUpRequestDto.getSocialType(), findSocialAccount.getSocialType());
        assertTrue(findMember.isRoomEscapeRecordsOpenYN(), "회원가입 시 방탈출 공개 여부는 기본적으로 true 여야 한다.");
        MemberProfileImage profileImage = findMember.getProfileImage();
        assertNull(profileImage, "회원가입 시 프로필 이미지 정보는 null 이어야 한다.");
        assertNull(findMember.getDescription(), "회원가입 시 자기소개는 null 이어야 한다.");
        assertNotNull(findMember.getRefreshToken(), "회원가입 시 Refresh Token 도 저장되어 있어야 한다.");
        assertNotNull(findMember.getRefreshTokenExpiredDate());

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
        Member findMember = memberService.getMember(signMemberId);

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
        assertThrows(MemberEmailDuplicateException.class, () -> authenticationService.signUp(memberSocialSignUpRequestDto2.toServiceDto()));

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
        assertThrows(MemberNicknameDuplicateException.class, () -> authenticationService.signUp(memberSocialSignUpRequestDto2.toServiceDto()));

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
        assertThrows(MemberSocialInfoDuplicateException.class, () -> authenticationService.signUp(memberSocialSignUpRequestDto2.toServiceDto()));

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
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd/hh/mm/ss");
        assertEquals(tokenDto.getRefreshTokenExpiredDate().format(dateTimeFormatter), refreshedTokenDto.getRefreshTokenExpiredDate().format(dateTimeFormatter));
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

    @Transactional
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

    @Transactional
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