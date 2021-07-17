package bbangduck.bd.bbangduck.domain.follow.repository;

import bbangduck.bd.bbangduck.domain.follow.entity.Follow;
import bbangduck.bd.bbangduck.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * 작성자 : 정구민 <br><br>
 *
 * 회원 친구 Entity 에 대한 간단한 쿼리를 적용하기 위한 Repository
 */
public interface FollowRepository extends JpaRepository<Follow, Long> {
    Optional<Follow> findByFollowingMemberAndFollowedMember(Member followingMember, Member followedMember);
}
