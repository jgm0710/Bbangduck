package bbangduck.bd.bbangduck.domain.theme.repository;

import bbangduck.bd.bbangduck.domain.theme.entity.ThemeGenre;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * ThemeGenreEntity  의 기본적인 DB 조작을 위해 구현한 Repository
 *
 * @author jgm
 */
public interface ThemeGenreRepository extends JpaRepository<ThemeGenre, Long> {
}
