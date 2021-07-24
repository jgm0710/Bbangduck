package bbangduck.bd.bbangduck.domain.auth.service;

import bbangduck.bd.bbangduck.domain.member.entity.Member;
import bbangduck.bd.bbangduck.domain.member.entity.SocialAccount;
import bbangduck.bd.bbangduck.domain.member.enumerate.MemberRole;
import bbangduck.bd.bbangduck.domain.member.enumerate.SocialType;
import bbangduck.bd.bbangduck.domain.member.service.MemberService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

class AuthenticationApplicationServiceUnitTest {

    MemberService memberService = Mockito.mock(MemberService.class);
    AuthenticationService authenticationService = Mockito.mock(AuthenticationService.class);
    KakaoSignInService kakaoSignInService = Mockito.mock(KakaoSignInService.class);

    AuthenticationApplicationService authenticationApplicationService = new AuthenticationApplicationService(
            memberService,
            authenticationService,
            kakaoSignInService
    );

    @Test
    @DisplayName("회원 탈퇴")
    public void withdrawal() {
        //given
        Member member = Member.builder()
                .id(1L)
                .roles(Set.of(MemberRole.USER))
                .build();

        SocialAccount socialAccount = SocialAccount.builder()
                .socialId("123798217")
                .socialType(SocialType.KAKAO)
                .build();

        member.addSocialAccount(socialAccount);

        given(memberService.getMember(member.getId())).willReturn(member);

        //when
        authenticationApplicationService.withdrawal(member.getId(), member.getId());

        //then
        then(authenticationService).should(times(1)).checkIfManipulateOtherMembersInfo(member.getId(), member.getId());
        then(memberService).should(times(1)).getMember(member.getId());
        then(kakaoSignInService).should(times(1)).disconnectFromKakao(socialAccount.getSocialId());

        assertFalse(member.getRoles().contains(MemberRole.USER), "유저 권한 없음");
        assertTrue(member.getRoles().contains(MemberRole.WITHDRAWAL), "회원 탈퇴 상태로 변경됨.");

    }

}