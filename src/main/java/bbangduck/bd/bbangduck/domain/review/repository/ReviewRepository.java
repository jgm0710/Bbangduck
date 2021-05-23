package bbangduck.bd.bbangduck.domain.review.repository;

import bbangduck.bd.bbangduck.domain.review.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

// TODO: 2021-05-23 주석 달기
public interface ReviewRepository extends JpaRepository<Review, Long> {
}
