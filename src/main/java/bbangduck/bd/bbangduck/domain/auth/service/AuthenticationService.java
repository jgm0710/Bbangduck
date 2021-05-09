package bbangduck.bd.bbangduck.domain.auth.service;

import bbangduck.bd.bbangduck.domain.auth.JwtTokenProvider;
import bbangduck.bd.bbangduck.domain.auth.dto.TokenDto;
import bbangduck.bd.bbangduck.domain.member.entity.Member;
import bbangduck.bd.bbangduck.domain.member.entity.SocialAccount;
import bbangduck.bd.bbangduck.domain.member.entity.SocialType;
import bbangduck.bd.bbangduck.domain.member.exception.MemberDuplicateException;
import bbangduck.bd.bbangduck.domain.member.exception.MemberNotFoundException;
import bbangduck.bd.bbangduck.domain.member.repository.MemberQueryRepository;
import bbangduck.bd.bbangduck.domain.member.repository.MemberRepository;
import bbangduck.bd.bbangduck.global.common.ResponseStatus;
import bbangduck.bd.bbangduck.global.config.properties.SecurityJwtProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 작성자 : 정구민 <br><br>
 *
 * 회원 인증에 대한 비즈니스 로직을 처리하기 위한 Service
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class AuthenticationService {

    private final JwtTokenProvider jwtTokenProvider;

    private final MemberRepository memberRepository;

    private final SecurityJwtProperties securityJwtProperties;

    private final MemberQueryRepository memberQueryRepository;

    public TokenDto signIn(Long memberId) {
        Member findMember = memberRepository.findById(memberId).orElseThrow(MemberNotFoundException::new);
        String email = findMember.getEmail();
        List<String> roleNameList = findMember.getRoleNameList();

        String jwtToken = jwtTokenProvider.createToken(email, roleNameList);
        TokenDto tokenDto = TokenDto.builder()
                .memberId(memberId)
                .accessToken(jwtToken)
                .accessTokenValidSecond(securityJwtProperties.getTokenValidSecond())
                .refreshToken(findMember.getRefreshToken())
                .refreshTokenExpiredDate(findMember.getRefreshTokenExpiredDate())
                .build();

        log.debug("Sign in by memberId");
        log.debug(tokenDto.toString());

        return tokenDto;
    }

    @Transactional
    public Long signUp(Member signUpMember) {
        checkSignUpDuplicate(signUpMember);
        Member savedMember = memberRepository.save(signUpMember);
        log.debug("savedMember : {}", savedMember);
        return savedMember.getId();
    }

    @Transactional
    public void withdrawal(Long memberId) {
        Member findMember = memberRepository.findById(memberId).orElseThrow(MemberNotFoundException::new);
        findMember.withdrawal();
    }

    private void checkSignUpDuplicate(Member signUpMember) {
        String signUpMemberEmail = signUpMember.getEmail();
        String signUpMemberNickname = signUpMember.getNickname();
        SocialAccount signUpMemberSocialAccount = signUpMember.getFirstSocialAccount();

        checkDuplicateEmails(signUpMemberEmail);
        checkDuplicateNickname(signUpMemberNickname);
        checkDuplicateSocialInfo(signUpMemberSocialAccount);
    }

    private void checkDuplicateNickname(String nickname) {
        if (memberRepository.findByNickname(nickname).isPresent()) {
            throw new MemberDuplicateException(ResponseStatus.MEMBER_NICKNAME_DUPLICATE);
        }
    }

    private void checkDuplicateEmails(String email) {
        if (memberRepository.findByEmail(email).isPresent()) {
            throw new MemberDuplicateException(ResponseStatus.MEMBER_EMAIL_DUPLICATE);
        }
    }

    private void checkDuplicateSocialInfo(SocialAccount socialAccount) {
        if (socialAccount == null || socialAccount.getSocialId() == null || socialAccount.getSocialId().isBlank() || socialAccount.getSocialType() == null) {
            return;
        }

        SocialType socialType = socialAccount.getSocialType();
        String socialId = socialAccount.getSocialId();

        if (memberQueryRepository.findBySocialTypeAndSocialId(socialType, socialId).isPresent()) {
            throw new MemberDuplicateException(ResponseStatus.MEMBER_SOCIAL_INFO_DUPLICATE);
        }
    }

}
