package bbangduck.bd.bbangduck.global.security;

import bbangduck.bd.bbangduck.domain.member.entity.Member;
import bbangduck.bd.bbangduck.domain.member.model.MemberRole;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

public class AccountAdapter extends User {

    private Member member;

    public AccountAdapter(Member member) {
        super(member.getEmail(), member.getPassword(), getMemberAuthorities(member.getRoles()));
        this.member = member;
    }

    private static Collection<? extends GrantedAuthority> getMemberAuthorities(Set<MemberRole> roles) {
        return roles.stream().map(memberRole -> new SimpleGrantedAuthority(memberRole.getRoleName())).collect(Collectors.toSet());
    }
}
