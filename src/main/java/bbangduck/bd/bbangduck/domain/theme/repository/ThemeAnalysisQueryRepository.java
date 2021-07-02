package bbangduck.bd.bbangduck.domain.theme.repository;

import bbangduck.bd.bbangduck.domain.theme.entity.ThemeAnalysis;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static bbangduck.bd.bbangduck.domain.theme.entity.QThemeAnalysis.themeAnalysis;

/**
 * 테마 분석에 대한 보다 복잡한 쿼리를 구현하기 위한 Repository
 *
 * @author jgm
 */
@Repository
@RequiredArgsConstructor
public class ThemeAnalysisQueryRepository {

    private final JPAQueryFactory queryFactory;

    public List<ThemeAnalysis> findByThemeId(Long themeId) {
        return queryFactory
                .selectFrom(themeAnalysis)
                .join(themeAnalysis.genre).fetchJoin()
                .where(themeAnalysis.theme.id.eq(themeId))
                .orderBy(
                        themeAnalysis.evaluatedCount.desc(),
                        themeAnalysis.genre.name.asc()
                )
                .fetch();
    }
}
