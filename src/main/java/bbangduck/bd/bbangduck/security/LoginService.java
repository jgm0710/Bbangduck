package bbangduck.bd.bbangduck.security;

import bbangduck.bd.bbangduck.member.Member;
import bbangduck.bd.bbangduck.member.MemberRepository;
import bbangduck.bd.bbangduck.member.exception.MemberNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoginService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Member findMember = memberRepository.findByEmail(email).orElseThrow(() -> new MemberNotFoundException(email));
        return new AccountAdapter(findMember);
    }
}
