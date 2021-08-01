package bbangduck.bd.bbangduck.domain.theme.repository;

import bbangduck.bd.bbangduck.domain.member.enumerate.MemberRole;
import bbangduck.bd.bbangduck.domain.theme.dto.service.ThemeGetPlayMemberListDto;
import bbangduck.bd.bbangduck.domain.theme.entity.QThemePlayMember;
import bbangduck.bd.bbangduck.domain.theme.entity.ThemePlayMember;
import bbangduck.bd.bbangduck.domain.theme.enumerate.ThemeGetMemberListSortCondition;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static bbangduck.bd.bbangduck.domain.theme.entity.QThemePlayMember.themePlayMember;

@Repository
@RequiredArgsConstructor
public class ThemePlayMemberQueryRepository {

    private final JPAQueryFactory queryFactory;

    public Optional<ThemePlayMember> findByThemeIdAndMemberId(Long themeId, Long memberId) {
        ThemePlayMember findThemePlayMember = queryFactory
                .selectFrom(QThemePlayMember.themePlayMember)
                .where(
                        themeIdEq(themeId),
                        memberIdEq(memberId)
                )
                .fetchOne();
        return Optional.ofNullable(findThemePlayMember);
    }

    public List<ThemePlayMember> findListByThemeId(Long themeId, ThemeGetPlayMemberListDto themeGetPlayMemberListDto) {
        return queryFactory
                .selectFrom(themePlayMember)
                .where(
                        themeIdEq(themeId),
                        themePlayMember.member.roles.contains(MemberRole.USER)
                )
                .orderBy(
                        themePlayMemberSortConditionEq(themeGetPlayMemberListDto.getSortCondition())
                )
                .offset(themeGetPlayMemberListDto.getOffset())
                .limit(themeGetPlayMemberListDto.getAmount())
                .fetch();
    }

    public long getThemePlayMembersCount(Long themeId) {
        return queryFactory
                .selectFrom(themePlayMember)
                .where(
                        themeIdEq(themeId),
                        themePlayMember.member.roles.contains(MemberRole.USER)
                )
                .fetchCount();
    }

    private OrderSpecifier<?> themePlayMemberSortConditionEq(ThemeGetMemberListSortCondition sortCondition) {
        switch (sortCondition) {
            case LATEST:
                return themePlayMember.lastPlayDateTime.desc();
            case OLDEST:
                return themePlayMember.lastPlayDateTime.asc();
            case REVIEW_LIKE_COUNT_ASC:
                return themePlayMember.reviewLikeCount.asc();
            default:
                return themePlayMember.reviewLikeCount.desc();
        }
    }

    private BooleanExpression memberIdEq(Long memberId) {
        return themePlayMember.member.id.eq(memberId);
    }

    private BooleanExpression themeIdEq(Long themeId) {
        return themePlayMember.theme.id.eq(themeId);
    }
}
