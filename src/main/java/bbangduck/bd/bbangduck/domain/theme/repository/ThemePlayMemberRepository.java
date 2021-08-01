package bbangduck.bd.bbangduck.domain.theme.repository;

import bbangduck.bd.bbangduck.domain.member.entity.Member;
import bbangduck.bd.bbangduck.domain.theme.entity.Theme;
import bbangduck.bd.bbangduck.domain.theme.entity.ThemePlayMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ThemePlayMemberRepository extends JpaRepository<ThemePlayMember, Long> {

    Optional<ThemePlayMember> findByThemeAndMember(Theme theme, Member member);

}
