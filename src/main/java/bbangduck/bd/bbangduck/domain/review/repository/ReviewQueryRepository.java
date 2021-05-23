package bbangduck.bd.bbangduck.domain.review.repository;

import bbangduck.bd.bbangduck.domain.review.entity.QReview;
import bbangduck.bd.bbangduck.domain.review.entity.Review;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static bbangduck.bd.bbangduck.domain.review.entity.QReview.review;

// TODO: 2021-05-23 주석 달기
@Repository
@RequiredArgsConstructor
public class ReviewQueryRepository {

    private final JPAQueryFactory queryFactory;

    public List<Review> findByMember(Long memberId) {
        List<Review> reviews = queryFactory
                .selectFrom(review)
                .where(review.member.id.eq(memberId))
                .fetch();

        return reviews;
    }
}
