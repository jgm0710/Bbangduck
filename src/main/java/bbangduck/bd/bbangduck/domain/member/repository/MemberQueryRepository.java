package bbangduck.bd.bbangduck.domain.member.repository;

import bbangduck.bd.bbangduck.domain.member.entity.Member;
import bbangduck.bd.bbangduck.domain.member.enumerate.MemberSearchKeywordType;
import bbangduck.bd.bbangduck.domain.member.enumerate.SocialType;
import bbangduck.bd.bbangduck.global.common.CriteriaDto;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static bbangduck.bd.bbangduck.domain.member.entity.QMember.member;
import static bbangduck.bd.bbangduck.domain.member.entity.QSocialAccount.socialAccount;


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
        Member findMember = queryFactory
                .select(socialAccount.member)
                .from(socialAccount)
                .join(socialAccount.member)
                .where(
                        socialAccount.socialType.eq(socialType)
                                .and(socialAccount.socialId.eq(socialId))
                ).fetchFirst();

        return Optional.ofNullable(findMember);
    }

    public Optional<Member> findByRefreshToken(String refreshToken) {
        Member findMember = queryFactory
                .selectFrom(member)
                .where(member.refreshInfo.refreshToken.eq(refreshToken))
                .fetchFirst();
        return Optional.ofNullable(findMember);
    }

    public List<Member> findAll(CriteriaDto criteriaDto) {
        return queryFactory
                .selectFrom(member)
                .offset(criteriaDto.getOffset())
                .limit(criteriaDto.getAmount())
                .fetch();
    }

    public Optional<Member> findBySearchTypeAndKeyword(MemberSearchKeywordType searchType, String keyword) {
        Member findMember = queryFactory
                .selectFrom(member)
                .where(
                        searchTypeAndKeywordEq(searchType, keyword)
                )
                .fetchFirst();
        return Optional.ofNullable(findMember);
    }

    private BooleanExpression searchTypeAndKeywordEq(MemberSearchKeywordType searchType, String keyword) {
        if (searchType == MemberSearchKeywordType.EMAIL) {
            return member.email.eq(keyword);
        }
        return member.nickname.eq(keyword);
    }

    public List<Member> findByMemberIds(List<Long> memberIds) {
        return queryFactory
                .selectFrom(member)
                .where(
                        memberIdsIn(memberIds)
                )
                .fetch();
    }

    private BooleanExpression memberIdsIn(List<Long> memberIds) {
        return member.id.in(memberIds);
    }
}
