package bbangduck.bd.bbangduck.domain.genre.service;

import bbangduck.bd.bbangduck.common.BaseTest;
import bbangduck.bd.bbangduck.domain.genre.entity.Genre;
import bbangduck.bd.bbangduck.domain.genre.repository.GenreRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@DisplayName("GenreService 통합 테스트")
class GenreServiceIntegrationTest extends BaseTest {

    @Autowired
    GenreService genreService;

    @Autowired
    GenreRepository genreRepository;

    @Test
    @DisplayName("장르 코드 목록을 통해 장르 목록 조회")
    public void getGenresByCodes() {
        //given
        Genre genre1 = Genre.builder()
                .code("gbcgr1")
                .name("genre1")
                .build();

        Genre genre2 = Genre.builder()
                .code("gbcgr2")
                .name("genre2")
                .build();

        Genre save1 = genreRepository.save(genre1);
        Genre save2 = genreRepository.save(genre2);

        List<String> genreCodes = List.of(save1.getCode(), save2.getCode());

        //when
        List<Genre> genres = genreService.getGenresByCodes(genreCodes);

        //then
        genres.forEach(genre -> {
            boolean anyMatch = genreCodes.stream().anyMatch(genreCode -> genreCode.equals(genre.getCode()));
            Assertions.assertTrue(anyMatch, "장르 목록 조회를 위해 기입한 장르 코드 목록 중 하나는 조회된 장르의 코드와 일치해야한다.");
        });

    }
}