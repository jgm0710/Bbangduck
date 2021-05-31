package bbangduck.bd.bbangduck.domain.member.repository;

import bbangduck.bd.bbangduck.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * 작성자 : 정구민 <br><br>
 *
 * 회원에 대한 간단한 쿼리를 쉽게 사용하기 위한 Repository
 */
public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByEmail(String email);

    Optional<Member> findByNickname(String nickname);
}
