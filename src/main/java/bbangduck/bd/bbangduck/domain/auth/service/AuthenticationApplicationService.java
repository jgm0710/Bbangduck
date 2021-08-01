package bbangduck.bd.bbangduck.domain.auth.service;

import bbangduck.bd.bbangduck.domain.member.entity.Member;
import bbangduck.bd.bbangduck.domain.member.entity.SocialAccount;
import bbangduck.bd.bbangduck.domain.member.enumerate.SocialType;
import bbangduck.bd.bbangduck.domain.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 회원 인증과 관련된 비즈니스 로직을 호출하여 통합적으로 처리하기 위한 Application Service
 *
 * @author Gumin Jeong
 * @since 2021-07-24
 */
@Service
@RequiredArgsConstructor
public class AuthenticationApplicationService {

    private final MemberService memberService;

    private final AuthenticationService authenticationService;

    private final KakaoSignInService kakaoSignInService;

    // TODO: 2021-07-24 회원 탈퇴 이후 일정 기간 지나면 회원 정보 말소하도록 배치 구현
    @Transactional
    public void withdrawal(Long authenticatedMemberId, Long memberId) {
        authenticationService.checkIfManipulateOtherMembersInfo(authenticatedMemberId, memberId);
        Member member = memberService.getMember(memberId);
        member.withdrawal();
        disconnectSocialAccount(member.getSocialAccounts());
    }

    private void disconnectSocialAccount(List<SocialAccount> socialAccounts) {
        socialAccounts.forEach(socialAccount -> {
            SocialType socialType = socialAccount.getSocialType();

            switch (socialType) {
                case KAKAO:
                    kakaoSignInService.disconnectFromKakao(socialAccount.getSocialId());
                    break;
                case NAVER:
                    // 네이버는 별도의 연동 해제 처리를 하지 않음
                    break;
            }
        });
    }
}
