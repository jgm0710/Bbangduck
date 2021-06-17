package bbangduck.bd.bbangduck.domain.member.repository;

import bbangduck.bd.bbangduck.domain.member.entity.MemberFriend;
import bbangduck.bd.bbangduck.domain.member.enumerate.MemberFriendState;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static bbangduck.bd.bbangduck.domain.member.entity.QMemberFriend.*;

/**
 * 작성자 : 정구민 <br><br>
 *
 * 회원 친구 Entity 에 대한 보다 복잡한 쿼리를 적용하기 위한 Repository
 */
@Repository
@RequiredArgsConstructor
public class MemberFriendQueryRepository {

    private final JPAQueryFactory queryFactory;

    public Optional<MemberFriend> findAllowedFriendByMemberAndFriend(Long memberId, Long friendId) {
        MemberFriend result = queryFactory
                .select(memberFriend)
                .from(memberFriend)
                .where(
                        memberIdEq(memberId),
                        friendIdEq(friendId),
                        stateEq(MemberFriendState.ALLOW)
                )
                .fetchFirst();

        return Optional.ofNullable(result);
    }

    private BooleanExpression stateEq(MemberFriendState state) {
        return memberFriend.state.eq(state);
    }

    private BooleanExpression friendIdEq(Long friendId) {
        return memberFriend.friend.id.eq(friendId);
    }

    private BooleanExpression memberIdEq(Long memberId) {
        return memberFriend.member.id.eq(memberId);
    }
}
