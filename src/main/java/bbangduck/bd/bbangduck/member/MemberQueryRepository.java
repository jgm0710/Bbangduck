package bbangduck.bd.bbangduck.member;

import bbangduck.bd.bbangduck.member.social.SocialType;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static bbangduck.bd.bbangduck.member.social.QSocialAccount.socialAccount;

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
                        socialAccount.socialType.eq(socialType).and(
                                socialAccount.socialId.eq(socialId)
                        )
                ).fetchFirst();

        return Optional.ofNullable(member);
    }
}
