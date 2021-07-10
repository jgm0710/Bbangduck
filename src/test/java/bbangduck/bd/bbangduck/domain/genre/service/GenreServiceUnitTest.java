package bbangduck.bd.bbangduck.domain.genre.service;

import bbangduck.bd.bbangduck.domain.genre.exception.GenreNotFoundException;
import bbangduck.bd.bbangduck.domain.genre.repository.GenreRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@DisplayName("GenreService 단위 테스트")
class GenreServiceUnitTest {

    GenreRepository genreMockRepository = Mockito.mock(GenreRepository.class);

    GenreService genreMockService = new GenreService(genreMockRepository);

    @Test
    @DisplayName("장르 코드 목록을 통해 장르 목록 조회 - 장르를 찾을 수 없는 경우")
    public void getGenresByCodes_GenreNotFound() {
        //given
        given(genreMockRepository.findByCode(any())).willReturn(Optional.empty());

        //when

        //then
        Assertions.assertThrows(GenreNotFoundException.class, () -> genreMockService.getGenresByCodes(List.of("genrecode1")));

    }

}