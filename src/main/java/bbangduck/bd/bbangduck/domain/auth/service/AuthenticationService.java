package bbangduck.bd.bbangduck.domain.auth.service;

import bbangduck.bd.bbangduck.domain.member.dto.MemberSignUpDto;
import bbangduck.bd.bbangduck.domain.member.dto.TokenDto;
import bbangduck.bd.bbangduck.domain.member.entity.Member;
import bbangduck.bd.bbangduck.domain.member.exception.MemberEmailDuplicateException;
import bbangduck.bd.bbangduck.domain.member.exception.MemberNicknameDuplicateException;
import bbangduck.bd.bbangduck.domain.member.exception.MemberNotFoundException;
import bbangduck.bd.bbangduck.domain.member.repository.MemberRepository;
import bbangduck.bd.bbangduck.global.common.ResponseStatus;
import bbangduck.bd.bbangduck.global.config.properties.JwtSecurityProperties;
import bbangduck.bd.bbangduck.domain.auth.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    private final JwtSecurityProperties jwtSecurityProperties;

    public TokenDto signIn(Long memberId) {
        Member findMember = memberRepository.findById(memberId).orElseThrow(MemberNotFoundException::new);
        String jwtToken = jwtTokenProvider.createToken(findMember.getEmail(), findMember.getRoleNameList());

        TokenDto tokenDto = TokenDto.builder()
                .accessToken(jwtToken)
                .accessTokenValidSecond(jwtSecurityProperties.getTokenValidSecond())
                .refreshToken(findMember.getRefreshInfo().getRefreshToken())
                .refreshTokenExpiredDate(findMember.getRefreshInfo().getRefreshTokenExpiredDate())
                .build();

        log.debug("Sign in by memberId");
        log.debug(tokenDto.toString());

        return tokenDto;
    }

    @Transactional
    public Long signUp(Member signUpMember) {
        checkDuplicate(signUpMember);
        Member savedMember = memberRepository.save(signUpMember);
        log.debug("savedMember : {}", savedMember);
        return savedMember.getId();
    }

    private void checkDuplicate(Member signUpMember) {
        checkDuplicateEmails(signUpMember.getEmail());
        checkDuplicateNickname(signUpMember.getNickname());
    }

    private void checkDuplicateNickname(String nickname) {
        Member findByNickname = memberRepository.findByNickname(nickname).orElse(null);
        if (findByNickname != null) {
            throw new MemberNicknameDuplicateException(ResponseStatus.MEMBER_NICKNAME_DUPLICATE);
        }
    }

    private void checkDuplicateEmails(String email) {
        Member findByEmail = memberRepository.findByEmail(email).orElse(null);
        if (findByEmail != null) {
            throw new MemberEmailDuplicateException(ResponseStatus.MEMBER_EMAIL_DUPLICATE);
        }
    }

}
