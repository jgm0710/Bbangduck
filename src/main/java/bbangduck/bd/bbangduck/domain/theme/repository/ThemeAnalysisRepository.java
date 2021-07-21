package bbangduck.bd.bbangduck.domain.theme.repository;

import bbangduck.bd.bbangduck.domain.genre.Genre;
import bbangduck.bd.bbangduck.domain.theme.entity.Theme;
import bbangduck.bd.bbangduck.domain.theme.entity.ThemeAnalysis;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * 테마 분석에 대한 기본적인 DB 조작을 위해 구현한 Repository
 *
 * @author jgm
 */
public interface ThemeAnalysisRepository extends JpaRepository<ThemeAnalysis, Long> {
    Optional<ThemeAnalysis> findByThemeAndGenre(Theme theme, Genre genre);
}
