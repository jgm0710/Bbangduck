package bbangduck.bd.bbangduck.member;

import bbangduck.bd.bbangduck.member.dto.TokenDto;
import bbangduck.bd.bbangduck.member.exception.MemberNotFoundException;
import bbangduck.bd.bbangduck.security.jwt.JwtSecurityProperties;
import bbangduck.bd.bbangduck.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class SignInService {

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
}
