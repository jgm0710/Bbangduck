package bbangduck.bd.bbangduck.domain.member.repository;

import bbangduck.bd.bbangduck.domain.member.entity.Member;
import bbangduck.bd.bbangduck.domain.member.entity.QMember;
import bbangduck.bd.bbangduck.domain.member.entity.SocialType;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;


import static bbangduck.bd.bbangduck.domain.member.entity.QMember.*;
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
}
