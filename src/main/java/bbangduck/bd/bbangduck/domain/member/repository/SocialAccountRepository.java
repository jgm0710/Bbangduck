package bbangduck.bd.bbangduck.domain.member.repository;

import bbangduck.bd.bbangduck.domain.member.entity.SocialAccount;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 작성자 : 정구민 <br><br>
 *
 * SocialAccount 의 DB 조작을 다루기 위한 Repository
 */
public interface SocialAccountRepository extends JpaRepository<SocialAccount, Long> {
}
