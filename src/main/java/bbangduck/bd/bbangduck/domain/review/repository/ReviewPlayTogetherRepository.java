package bbangduck.bd.bbangduck.domain.review.repository;

import bbangduck.bd.bbangduck.domain.review.entity.ReviewPlayTogether;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 작성자 : JGM <br>
 * 작성 일자 : 2021-06-12 <br><br>
 *
 * 리뷰에 등록되는 함께 플레이한 친구 Entity 에 대한 기본적인 DB 조작을 하기 위해 구현한 Repository
 */
public interface ReviewPlayTogetherRepository extends JpaRepository<ReviewPlayTogether, Long> {
}
