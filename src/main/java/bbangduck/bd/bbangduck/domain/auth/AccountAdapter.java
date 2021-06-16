package bbangduck.bd.bbangduck.domain.auth;

import bbangduck.bd.bbangduck.domain.member.entity.Member;
import bbangduck.bd.bbangduck.domain.member.enumerate.MemberRole;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 작성자 : 정구민 <br><br>
 *
 * 인증에 대한 회원 정보를 담기 위한 User 상속 Class
 */
public class AccountAdapter extends User {

    private Member member;

    public AccountAdapter(Member member) {
        super(member.getEmail(), getMemberPassword(member.getPassword()), getMemberAuthorities(member.getRoles()));
        this.member = member;
    }

    public Member getMember() {
        return member;
    }

    private static String getMemberPassword(String password) {
        return password == null ? "" : password;
    }

    private static Collection<? extends GrantedAuthority> getMemberAuthorities(Set<MemberRole> roles) {
        return roles.stream().map(memberRole -> new SimpleGrantedAuthority(memberRole.getRoleName())).collect(Collectors.toSet());
    }
}
