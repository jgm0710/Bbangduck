package bbangduck.bd.bbangduck.domain.review.repository;

import bbangduck.bd.bbangduck.domain.review.entity.ReviewLike;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 작성자 : 정구민 <br><br>
 *
 * 리뷰 좋아요에 대한 간단한 CRUD DB 동작을 위해 만든 Repository
 */
public interface ReviewLikeRepository extends JpaRepository<ReviewLike, Long> {
}
