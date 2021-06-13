package bbangduck.bd.bbangduck.domain.review.repository;

import bbangduck.bd.bbangduck.domain.review.entity.ReviewDetail;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 작성자 : 정구민 <br>
 * 작성 일자 : 2021-06-14 <br><br>
 *
 * 리뷰 상세 Entity 에 대한 기본적인 DB 조작을 위해 구현한 Repository
 */
public interface ReviewDetailRepository extends JpaRepository<ReviewDetail, Long> {
}
