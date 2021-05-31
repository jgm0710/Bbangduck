package bbangduck.bd.bbangduck.domain.member.repository;

import bbangduck.bd.bbangduck.domain.member.entity.MemberPlayInclination;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static bbangduck.bd.bbangduck.domain.member.entity.QMemberPlayInclination.memberPlayInclination;

/**
 * 작성자 : 정구민 <br><br>
 *
 * 회원 플레이 성향에 대한 복잡한 쿼리를 작성하기 위해 구현한 QueryDsl Repository
 */
@Repository
@RequiredArgsConstructor
public class MemberPlayInclinationQueryRepository {

    private final JPAQueryFactory queryFactory;

    public List<MemberPlayInclination> findTopByMember(Long memberId, long limit) {
        return queryFactory
                .select(memberPlayInclination)
                .where(memberIdEq(memberId))
                .orderBy(playCountDesc())
                .offset(0)
                .limit(limit)
                .fetch();
    }

    public List<MemberPlayInclination> findAllByMember(Long memberId) {
        return queryFactory
                .select(memberPlayInclination)
                .where(memberIdEq(memberId))
                .orderBy(playCountDesc())
                .fetch();

    }

    private OrderSpecifier<Integer> playCountDesc() {
        return memberPlayInclination.playCount.desc();
    }

    private BooleanExpression memberIdEq(Long memberId) {
        return memberPlayInclination.member.id.eq(memberId);
    }
}
