package bbangduck.bd.bbangduck.domain.member.repository;

import bbangduck.bd.bbangduck.domain.member.entity.Member;
import bbangduck.bd.bbangduck.domain.member.entity.QMember;
import bbangduck.bd.bbangduck.domain.member.entity.SocialType;
import bbangduck.bd.bbangduck.domain.review.entity.QReview;
import bbangduck.bd.bbangduck.domain.review.entity.Review;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


import static bbangduck.bd.bbangduck.domain.member.entity.QMember.*;
import static bbangduck.bd.bbangduck.domain.member.entity.QSocialAccount.socialAccount;
import static bbangduck.bd.bbangduck.domain.review.entity.QReview.*;


/**
 * 작성자 : 정구민 <br><br>
 *
 * 회원에 대한 복잡한 쿼리를 구현하기 위한 Repository
 */
@Repository
@RequiredArgsConstructor
public class MemberQueryRepository {

    private final JPAQueryFactory queryFactory;

    public Optional<Member> findBySocialTypeAndSocialId(SocialType socialType, String socialId) {
        Member member = queryFactory
                .select(socialAccount.member)
                .from(socialAccount)
                .join(socialAccount.member)
                .where(
                        socialAccount.socialType.eq(socialType)
                                .and(socialAccount.socialId.eq(socialId))
                ).fetchFirst();

        return Optional.ofNullable(member);
    }

    public Optional<Member> findByRefreshToken(String refreshToken) {
        Member member = queryFactory
                .selectFrom(QMember.member)
                .where(QMember.member.refreshInfo.refreshToken.eq(refreshToken))
                .fetchFirst();
        return Optional.ofNullable(member);
    }

    public void test(Long memberId, Long reviewId) {
        Review review = queryFactory
                .select(QReview.review)
                .from(QReview.review)
                .join(member).fetchJoin()
                .where(QReview.review.id.eq(reviewId))
                .fetchFirst();

        queryFactory
                .selectFrom(QReview.review)
                .where(QReview.review.member.id.eq(memberId));

        Member member = review.getMember();
        member.getId();
        member.getNickname();
        member.getEmail();
    }
}
