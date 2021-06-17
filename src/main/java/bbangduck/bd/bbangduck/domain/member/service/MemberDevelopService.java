package bbangduck.bd.bbangduck.domain.member.service;

import bbangduck.bd.bbangduck.domain.auth.JwtTokenProvider;
import bbangduck.bd.bbangduck.domain.auth.dto.service.MemberSignInDto;
import bbangduck.bd.bbangduck.domain.auth.dto.service.TokenDto;
import bbangduck.bd.bbangduck.domain.member.entity.Member;
import bbangduck.bd.bbangduck.domain.member.entity.SocialAccount;
import bbangduck.bd.bbangduck.domain.member.exception.MemberNotFoundException;
import bbangduck.bd.bbangduck.domain.member.repository.MemberProfileImageRepository;
import bbangduck.bd.bbangduck.domain.member.repository.MemberQueryRepository;
import bbangduck.bd.bbangduck.domain.member.repository.MemberRepository;
import bbangduck.bd.bbangduck.domain.member.repository.SocialAccountRepository;
import bbangduck.bd.bbangduck.global.common.CriteriaDto;
import bbangduck.bd.bbangduck.global.config.properties.SecurityJwtProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 작성자 : 정구민 <br><br>
 *
 * 프론트 개발자 편의 기능을 제공하기 위해 필요한 요청에 대해
 * 처리해야 할 서비스 로직을 구현한 Service
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberDevelopService{

    private final MemberRepository memberRepository;

    private final MemberQueryRepository memberQueryRepository;

    private final MemberProfileImageRepository memberProfileImageRepository;

    private final SocialAccountRepository socialAccountRepository;

    private final JwtTokenProvider jwtTokenProvider;

    private final SecurityJwtProperties securityJwtProperties;


    @Transactional
    public void deleteMember(Long memberId) {
        Member findMember = memberRepository.findById(memberId).orElseThrow(MemberNotFoundException::new);
        List<SocialAccount> socialAccounts = findMember.getSocialAccounts();
        if (!socialAccounts.isEmpty()) {
            socialAccounts.forEach(socialAccount -> socialAccountRepository.delete(socialAccount));
        }

        if (findMember.getProfileImage() != null) {
            findMember.deleteProfileImage(memberProfileImageRepository);
        }

        memberRepository.delete(findMember);
    }

    public TokenDto signInDeveloper(MemberSignInDto memberSignInDto) {
        String email = memberSignInDto.getEmail();
        Member member = memberRepository.findByEmail(email).orElseThrow(() -> new MemberNotFoundException(email));
        member.signIn(securityJwtProperties.getRefreshTokenExpiredDate());
        String token = jwtTokenProvider.createToken(member.getEmail(), member.getRoleNameList());
        return TokenDto.builder()
                .memberId(member.getId())
                .accessToken(token)
                .accessTokenValidSecond(securityJwtProperties.getTokenValidSecond())
                .refreshToken(member.getRefreshToken())
                .refreshTokenExpiredDate(member.getRefreshTokenExpiredDate())
                .build();
    }

    public List<Member> getMemberList(CriteriaDto criteriaDto) {
        return memberQueryRepository.findAll(criteriaDto);
    }
}
