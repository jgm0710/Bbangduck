package bbangduck.bd.bbangduck.domain.theme.repository;

import bbangduck.bd.bbangduck.domain.theme.entity.Theme;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 작성자 : 정구민 <br><br>
 *
 * 테마에 대한 기본적인 DB 조작을 하기 위한 Repository
 */
public interface ThemeRepository extends JpaRepository<Theme, Long> {

}
