package bbangduck.bd.bbangduck.domain.auth.service;

import bbangduck.bd.bbangduck.domain.member.dto.MemberSignUpDto;
import bbangduck.bd.bbangduck.domain.member.dto.TokenDto;
import bbangduck.bd.bbangduck.domain.member.entity.Member;
import bbangduck.bd.bbangduck.domain.member.exception.MemberNotFoundException;
import bbangduck.bd.bbangduck.domain.member.repository.MemberRepository;
import bbangduck.bd.bbangduck.global.config.properties.JwtSecurityProperties;
import bbangduck.bd.bbangduck.domain.auth.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public Long signUp(MemberSignUpDto signUpDto) {
        Member signUpMember = Member.signUp(signUpDto, jwtSecurityProperties);
        Member savedMember = memberRepository.save(signUpMember);
        log.debug("savedMember : {}", savedMember);
        return savedMember.getId();
    }

}
