package bbangduck.bd.bbangduck.domain.friend.repository;

import bbangduck.bd.bbangduck.domain.friend.entity.MemberFriend;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 작성자 : 정구민 <br><br>
 *
 * 회원 친구 Entity 에 대한 간단한 쿼리를 적용하기 위한 Repository
 */
public interface MemberFriendRepository extends JpaRepository<MemberFriend, Long> {
}
