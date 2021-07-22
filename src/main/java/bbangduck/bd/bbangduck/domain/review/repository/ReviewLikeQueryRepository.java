package bbangduck.bd.bbangduck.domain.review.repository;

import bbangduck.bd.bbangduck.domain.review.entity.QReviewLike;
import bbangduck.bd.bbangduck.domain.review.entity.ReviewLike;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static bbangduck.bd.bbangduck.domain.review.entity.QReviewLike.reviewLike;

/**
 * 작성자 : 정구민 <br><br>
 *
 * 리뷰 좋아요에 대한 보다 복잡한 쿼리를 작성하기 위해 구현한 Repository
 */
@Repository
@RequiredArgsConstructor
public class ReviewLikeQueryRepository {

    private final JPAQueryFactory queryFactory;

    public Optional<ReviewLike> findByMemberIdAndReviewId(Long memberId, Long reviewId) {
        ReviewLike reviewLike = queryFactory
                .selectFrom(QReviewLike.reviewLike)
                .where(
                        QReviewLike.reviewLike.member.id.eq(memberId),
                        QReviewLike.reviewLike.review.id.eq(reviewId)
                )
                .fetchOne();
        return Optional.ofNullable(reviewLike);
    }
}
