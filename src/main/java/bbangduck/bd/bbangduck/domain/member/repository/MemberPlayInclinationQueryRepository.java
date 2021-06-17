package bbangduck.bd.bbangduck.domain.member.repository;

import bbangduck.bd.bbangduck.domain.genre.entity.QGenre;
import bbangduck.bd.bbangduck.domain.member.entity.MemberPlayInclination;
import bbangduck.bd.bbangduck.domain.member.entity.QMember;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static bbangduck.bd.bbangduck.domain.genre.entity.QGenre.genre;
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
        QMember member = QMember.member;
        QGenre genre = QGenre.genre;
        return queryFactory
                .selectFrom(memberPlayInclination)
                .join(memberPlayInclination.member, member).fetchJoin()
                .join(memberPlayInclination.genre, genre).fetchJoin()
                .where(memberIdEq(memberId))
                .orderBy(
                        playCountDesc(),
                        genre.name.asc()
                )
                .offset(0)
                .limit(limit)
                .fetch();
    }

    public List<MemberPlayInclination> findAllByMember(Long memberId) {
        QMember member = QMember.member;
        QGenre genre = QGenre.genre;
        return queryFactory
                .selectFrom(memberPlayInclination)
                .join(memberPlayInclination.member, member).fetchJoin()
                .join(memberPlayInclination.genre, genre).fetchJoin()
                .where(memberIdEq(memberId))
                .orderBy(
                        playCountDesc(),
                        genre.name.desc()
                )
                .fetch();
    }

    public Optional<MemberPlayInclination> findOneByMemberAndGenre(Long memberId, Long genreId) {
        MemberPlayInclination result = queryFactory
                .selectFrom(memberPlayInclination)
                .where(
                        memberIdEq(memberId),
                        genreIdEq(genreId)
                )
                .fetchFirst();

        return Optional.ofNullable(result);
    }

    private BooleanExpression genreIdEq(Long genreId) {
        return genre.id.eq(genreId);
    }

    private OrderSpecifier<Integer> playCountDesc() {
        return memberPlayInclination.playCount.desc();
    }

    private BooleanExpression memberIdEq(Long memberId) {
        return memberPlayInclination.member.id.eq(memberId);
    }
}
