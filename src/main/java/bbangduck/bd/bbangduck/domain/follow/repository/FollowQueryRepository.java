package bbangduck.bd.bbangduck.domain.follow.repository;

import bbangduck.bd.bbangduck.domain.follow.entity.Follow;
import bbangduck.bd.bbangduck.domain.follow.entity.QFollow;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static bbangduck.bd.bbangduck.domain.follow.entity.QFollow.follow;

/**
 * 팔로우와 관련된 복잡한 쿼리를 구현한 Repository
 *
 * @author Gumin Jeong
 * @since 2021-07-15
 */
@Repository
@RequiredArgsConstructor
public class FollowQueryRepository {

    private final JPAQueryFactory queryFactory;

    public Optional<Follow> findByFollowingMemberIdAndFollowedMemberId(Long followingMemberId, Long followedMemberId) {
        Follow follow = queryFactory
                .selectFrom(QFollow.follow)
                .where(
                        followingMemberIdEq(followingMemberId),
                        followedMemberIdEq(followedMemberId)
                )
                .fetchOne();
        return Optional.ofNullable(follow);
    }

    public List<Follow> findByFollowingMemberIdAndFollowedMemberIds(Long followingMemberId, List<Long> followedMemberIds) {
        return queryFactory
                .selectFrom(follow)
                .where(
                        followingMemberIdEq(followingMemberId),
                        followedMemberIdsIn(followedMemberIds)
                )
                .fetch();
    }

    public List<Follow> findByFollowingMemberIdsAndFollowedMemberId(List<Long> followingMemberIds, Long followedMemberId) {
        return queryFactory
                .selectFrom(follow)
                .where(
                        followingMemberIdsIn(followingMemberIds),
                        followedMemberIdEq(followedMemberId)
                )
                .fetch();
    }

    private BooleanExpression followedMemberIdEq(Long followedMemberId) {
        return follow.followedMember.id.eq(followedMemberId);
    }

    private BooleanExpression followingMemberIdsIn(List<Long> followingMemberIds) {
        return follow.followingMember.id.in(followingMemberIds);
    }

    private BooleanExpression followedMemberIdsIn(List<Long> followedMemberIds) {
        return follow.followedMember.id.in(followedMemberIds);
    }

    private BooleanExpression followingMemberIdEq(Long followingMemberId) {
        return follow.followingMember.id.eq(followingMemberId);
    }
}
