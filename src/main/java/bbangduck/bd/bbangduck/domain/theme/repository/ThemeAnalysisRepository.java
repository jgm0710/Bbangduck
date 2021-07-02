package bbangduck.bd.bbangduck.domain.theme.repository;

import bbangduck.bd.bbangduck.domain.theme.entity.ThemeAnalysis;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 테마 분석에 대한 기본적인 DB 조작을 위해 구현한 Repository
 *
 * @author jgm
 */
public interface ThemeAnalysisRepository extends JpaRepository<ThemeAnalysis, Long> {
}
