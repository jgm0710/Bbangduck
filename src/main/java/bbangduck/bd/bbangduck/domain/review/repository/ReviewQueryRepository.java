package bbangduck.bd.bbangduck.domain.review.repository;

import bbangduck.bd.bbangduck.domain.review.entity.Review;
import bbangduck.bd.bbangduck.domain.review.entity.dto.ReviewRecodesCountsDto;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

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

    public Optional<ReviewRecodesCountsDto> findRecodesCountsByMember(Long memberId) {
        ReviewRecodesCountsDto reviewRecodesCountsDto = queryFactory
                .select(
                        Projections.constructor(
                                ReviewRecodesCountsDto.class,
                                getTotalRecodesCountByMember(memberId),
                                getSuccessRecodesCountByMember(memberId),
                                getFailRecodesCountByMember(memberId)
                        )
                )
                .from(review)
                .fetchFirst();
        return Optional.ofNullable(reviewRecodesCountsDto);
    }

    private Expression<Integer> getFailRecodesCountByMember(Long memberId) {
        return ExpressionUtils.as(
                JPAExpressions
                        .select(review.count().intValue())
                        .from(review)
                        .where(
                                memberIdEq(memberId),
                                clearYnEq(false)
                        ),
                "failRecodesCount"
        );
    }

    private Expression<Integer> getSuccessRecodesCountByMember(Long memberId) {
        return ExpressionUtils.as(
                JPAExpressions
                        .select(review.count().intValue())
                        .from(review)
                        .where(
                                memberIdEq(memberId),
                                clearYnEq(true)
                        ),
                "successRecodesCount"
        );
    }

    private Expression<Integer> getTotalRecodesCountByMember(Long memberId) {
        return ExpressionUtils.as(
                JPAExpressions
                        .select(review.count().intValue())
                        .from(review)
                        .where(memberIdEq(memberId))
                ,
                "totalRecodesCount"
        );
    }

    private BooleanExpression clearYnEq(boolean clearYN) {
        return review.clearYN.eq(clearYN);
    }

    private BooleanExpression memberIdEq(Long memberId) {
        return review.member.id.eq(memberId);
    }
}
