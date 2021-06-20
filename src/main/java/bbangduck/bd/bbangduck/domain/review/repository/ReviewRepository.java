package bbangduck.bd.bbangduck.domain.review.repository;

import bbangduck.bd.bbangduck.domain.review.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 작성자 : 정구민 <br><br>
 * <p>
 * 리뷰에 대해 간단한 Insert, Select, Delete 등의 기능을 사용하기 위해 JpaRepository 를 상속 받은
 * Repository
 */
public interface ReviewRepository extends JpaRepository<Review, Long> {
}
