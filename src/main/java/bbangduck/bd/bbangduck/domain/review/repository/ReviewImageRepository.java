package bbangduck.bd.bbangduck.domain.review.repository;

import bbangduck.bd.bbangduck.domain.review.entity.ReviewImage;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 작성자 : JGM <br>
 * 작성 일자 : 2021-06-12 <br><br>
 *
 * 리뷰 이미지에 대한 기본적인 DB 조작을 하기 위해 구현한 Repository
 */
public interface ReviewImageRepository extends JpaRepository<ReviewImage, Long> {
}
