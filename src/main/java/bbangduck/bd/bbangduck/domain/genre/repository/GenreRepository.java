package bbangduck.bd.bbangduck.domain.genre.repository;

import bbangduck.bd.bbangduck.domain.genre.entity.Genre;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * 작성자 : 정구민 <br><br>
 *
 * 장르에 대한 DB 조작을 하기 위한 Repository
 */
public interface GenreRepository extends JpaRepository<Genre, Long> {

    Optional<Genre> findByCode(String code);

}
