package bbangduck.bd.bbangduck.domain.review.repository;

import bbangduck.bd.bbangduck.domain.review.entity.ReviewPerceivedThemeGenre;
import bbangduck.bd.bbangduck.domain.review.entity.ReviewSurvey;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *  * 작성자 : 정구민 <br>
 *  * 작성 일자 : 2021-06-11 <br><br>
 *
 *  리뷰 체감 장르 Entity 에 대한 기본적인 DB 조작을 위해 구현한 Repository
 */
public interface ReviewPerceivedThemeGenreRepository extends JpaRepository<ReviewPerceivedThemeGenre, Long> {
    void deleteByReviewSurvey(ReviewSurvey reviewSurvey);
}
