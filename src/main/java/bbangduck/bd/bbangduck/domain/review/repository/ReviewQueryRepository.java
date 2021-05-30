package bbangduck.bd.bbangduck.domain.review.repository;

import bbangduck.bd.bbangduck.domain.review.entity.QReview;
import bbangduck.bd.bbangduck.domain.review.entity.Review;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static bbangduck.bd.bbangduck.domain.review.entity.QReview.review;

/**
 * 작성자 : 정구민 <br><br>
 *
 * 리뷰에 대해 기본 JpaRepository 보다 복잡한 쿼리가 필요한 경우 사용할 Repository
 */
@Repository
@RequiredArgsConstructor
public class ReviewQueryRepository {

    private final JPAQueryFactory queryFactory;

    public List<Review> findByMember(Long memberId) {
        return queryFactory
                .selectFrom(review)
                .where(review.member.id.eq(memberId))
                .fetch();
    }
}
