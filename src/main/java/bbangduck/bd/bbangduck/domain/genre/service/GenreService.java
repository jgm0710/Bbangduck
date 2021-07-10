package bbangduck.bd.bbangduck.domain.genre.service;

import bbangduck.bd.bbangduck.domain.genre.entity.Genre;
import bbangduck.bd.bbangduck.domain.genre.exception.GenreNotFoundException;
import bbangduck.bd.bbangduck.domain.genre.repository.GenreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 장르와 관련된 비즈니스 로직을 구현한 Service
 *
 * @author Gumin Jeong
 * @since 2021-07-10
 */
@Service
@RequiredArgsConstructor
public class GenreService {

    private final GenreRepository genreRepository;

    public List<Genre> getGenresByCodes(List<String> genreCodes) {
        return genreCodes.stream()
                .map(genreCode -> genreRepository.findByCode(genreCode).orElseThrow(() -> new GenreNotFoundException(genreCode)))
                .collect(Collectors.toList());
    }
}
