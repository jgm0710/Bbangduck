package bbangduck.bd.bbangduck.domain.auth.service;

import bbangduck.bd.bbangduck.domain.auth.JwtTokenProvider;
import bbangduck.bd.bbangduck.domain.auth.exception.ManipulateOtherMembersInfoException;
import bbangduck.bd.bbangduck.domain.member.repository.MemberQueryRepository;
import bbangduck.bd.bbangduck.domain.member.repository.MemberRepository;
import bbangduck.bd.bbangduck.global.config.properties.SecurityJwtProperties;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

class AuthenticationServiceUnitTest {


    JwtTokenProvider jwtTokenProvider = mock(JwtTokenProvider.class);
    MemberRepository memberRepository = mock(MemberRepository.class);
    SecurityJwtProperties securityJwtProperties = mock(SecurityJwtProperties.class);
    MemberQueryRepository memberQueryRepository = mock(MemberQueryRepository.class);

    AuthenticationService authenticationService = new AuthenticationService(
            jwtTokenProvider,
            memberRepository,
            securityJwtProperties,
            memberQueryRepository
    );

    @Test
    @DisplayName("회원 조작 권한 체크")
    public void checkIfManipulateOtherMembersInfo() {
        //given
        Long authenticatedMemberId = 1L;
        Long manipulatedMemberId = 2L;


        //when

        //then
        assertThrows(ManipulateOtherMembersInfoException.class, () -> authenticationService.checkIfManipulateOtherMembersInfo(authenticatedMemberId, manipulatedMemberId));

    }
}