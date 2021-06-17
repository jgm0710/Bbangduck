package bbangduck.bd.bbangduck.domain.review.repository;

import bbangduck.bd.bbangduck.domain.review.entity.Review;
import bbangduck.bd.bbangduck.domain.review.dto.entity.ReviewRecodesCountsDto;
import bbangduck.bd.bbangduck.domain.review.enumerate.ReviewSearchType;
import bbangduck.bd.bbangduck.domain.review.enumerate.ReviewSortCondition;
import bbangduck.bd.bbangduck.domain.review.dto.service.ReviewSearchDto;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.*;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
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

    public QueryResults<Review> findListByTheme(Long themeId, ReviewSearchDto searchDto) {
        return queryFactory
                .selectFrom(review)
                .where(
                        themeIdEq(themeId),
                        deleteYnEq(false)
                )
                .offset(searchDto.getOffset())
                .limit(searchDto.getAmount())
                .orderBy(
                        sortConditionEq(searchDto.getSortCondition())
                )
                .fetchResults();
    }

    private BooleanExpression deleteYnEq(boolean deleteYN) {
        return review.deleteYN.eq(deleteYN);
    }

    private BooleanExpression themeIdEq(Long themeId) {
        return review.theme.id.eq(themeId);
    }

    private OrderSpecifier<?>[] sortConditionEq(ReviewSortCondition sortCondition) {
        List<OrderSpecifier<?>> orderSpecifiers = new ArrayList<>();

        switch (sortCondition) {
            case OLDEST:
                orderSpecifiers.add(review.registerTimes.asc());
                break;
            case LIKE_COUNT_DESC:
                orderSpecifiers.add(review.likeCount.desc());
                orderSpecifiers.add(reviewRegisterTimeDesc());
                break;
            case LIKE_COUNT_ASC:
                orderSpecifiers.add(review.likeCount.asc());
                orderSpecifiers.add(reviewRegisterTimeDesc());
                break;
            case RATING_DESC:
                orderSpecifiers.add(review.rating.desc());
                orderSpecifiers.add(reviewRegisterTimeDesc());
                break;
            case RATING_ASC:
                orderSpecifiers.add(review.rating.asc());
                orderSpecifiers.add(reviewRegisterTimeDesc());
                break;
            default:
                orderSpecifiers.add(reviewRegisterTimeDesc());
        }

        return orderSpecifiers.toArray(new OrderSpecifier[orderSpecifiers.size()]);
    }

    private OrderSpecifier<LocalDateTime> reviewRegisterTimeDesc() {
        return review.registerTimes.desc();
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
                                clearYnEq(false),
                                deleteYnEq(false)
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
                                clearYnEq(true),
                                deleteYnEq(false)
                        ),
                "successRecodesCount"
        );
    }

    private Expression<Integer> getTotalRecodesCountByMember(Long memberId) {
        return ExpressionUtils.as(
                JPAExpressions
                        .select(review.count().intValue())
                        .from(review)
                        .where(
                                memberIdEq(memberId),
                                deleteYnEq(false)
                        )
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

    public long decreaseRecodeNumberWhereInGreaterThenThisRecodeNumber(Long reviewId, Integer recodeNumber) {
        return queryFactory
                .update(review)
                .set(review.recodeNumber, review.recodeNumber.subtract(1))
                .where(
                        review.member.id.eq(
                                JPAExpressions
                                        .select(review.member.id)
                                        .from(review)
                                        .where(review.id.eq(reviewId))
                        ),
                        review.recodeNumber.gt(recodeNumber)
                )
                .execute()
        ;
    }

    public QueryResults<Review> findListByMember(Long memberId, ReviewSearchDto reviewSearchDto) {
        return queryFactory
                .selectFrom(review)
                .where(
                        memberIdEq(memberId),
                        searchTypeEq(reviewSearchDto.getSearchType()),
                        deleteYnEq(false)
                )
                .orderBy(
                        review.recodeNumber.desc()
                )
                .offset(reviewSearchDto.getOffset())
                .limit(reviewSearchDto.getAmount())
                .fetchResults();
    }

    private BooleanExpression searchTypeEq(ReviewSearchType searchType) {
        switch (searchType) {
            case SUCCESS:
                return clearYnEq(true);
            case FAIL:
                return clearYnEq(false);
            default:
                return null;
        }
    }
}
