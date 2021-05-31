package bbangduck.bd.bbangduck.domain.member.repository;

import bbangduck.bd.bbangduck.domain.member.entity.MemberProfileImage;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 작성자 : 정구민 <br><br>
 *
 * 회원의 프로필 이미지 추가, 수정, 삭제 를 위한 Repository
 */
public interface MemberProfileImageRepository extends JpaRepository<MemberProfileImage, Long> {
}
