package bbangduck.bd.bbangduck.domain.auth.service;

import bbangduck.bd.bbangduck.domain.auth.AccountAdapter;
import bbangduck.bd.bbangduck.domain.member.entity.Member;
import bbangduck.bd.bbangduck.domain.member.exception.MemberNotFoundException;
import bbangduck.bd.bbangduck.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * 작성자 : 정구민 <br><br>
 * 인증 시 이메일을 통한 회원 정보를 SecurityContext 의 Authentication 에 넣어주기 위해 구현한 UserDetailsService 구현 Class
 */
@Service
@RequiredArgsConstructor
public class AccountService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Member findMember = memberRepository.findByEmail(email).orElseThrow(() -> new MemberNotFoundException(email));
        return new AccountAdapter(findMember);
    }
}
