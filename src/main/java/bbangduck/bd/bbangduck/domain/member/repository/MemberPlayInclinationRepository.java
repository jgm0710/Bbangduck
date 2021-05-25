package bbangduck.bd.bbangduck.domain.member.repository;

import bbangduck.bd.bbangduck.domain.member.entity.MemberPlayInclination;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 작성자 : 정구민 <br><br>
 *
 * 회원의 성향에 대한 DB 조작을 하기 위한 Repository
 */
public interface MemberPlayInclinationRepository extends JpaRepository<MemberPlayInclination, Long> {
}
