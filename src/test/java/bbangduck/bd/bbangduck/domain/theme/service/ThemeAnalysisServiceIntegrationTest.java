package bbangduck.bd.bbangduck.domain.theme.service;

import bbangduck.bd.bbangduck.common.BaseTest;
import bbangduck.bd.bbangduck.domain.genre.entity.Genre;
import bbangduck.bd.bbangduck.domain.genre.repository.GenreRepository;
import bbangduck.bd.bbangduck.domain.theme.entity.Theme;
import bbangduck.bd.bbangduck.domain.theme.entity.ThemeAnalysis;
import bbangduck.bd.bbangduck.domain.theme.repository.ThemeAnalysisRepository;
import bbangduck.bd.bbangduck.domain.theme.repository.ThemeRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@DisplayName("ThemeAnalysisService 통합 테스트")
class ThemeAnalysisServiceIntegrationTest extends BaseTest {

    @Autowired
    ThemeAnalysisService themeAnalysisService;

    @Autowired
    GenreRepository genreRepository;

    @Autowired
    ThemeRepository themeRepository;

    @Autowired
    ThemeAnalysisRepository themeAnalysisRepository;

    @Test
    @DisplayName("테마 분석 반영 - 기존에 플레이했던 장르일 경우")
    public void reflectingThemeAnalyses_AlreadyPlayedGenres() {
        //given
        Theme theme = Theme.builder()
                .name("theme")
                .build();
        Theme savedTheme = themeRepository.save(theme);

        Genre genre1 = Genre.builder()
                .code("rtagr1")
                .name("genre1")
                .build();

        Genre genre2 = Genre.builder()
                .code("rtagr2")
                .name("genre2")
                .build();

        Genre savedGenre1 = genreRepository.save(genre1);
        Genre savedGenre2 = genreRepository.save(genre2);

        List<Genre> genres = List.of(savedGenre1, savedGenre2);

        //when
        themeAnalysisService.reflectingThemeAnalyses(theme, genres);
        themeAnalysisService.reflectingThemeAnalyses(theme, genres);

        //then
        ThemeAnalysis themeAnalysis1 = themeAnalysisRepository.findByThemeAndGenre(theme, genre1).orElseThrow(EntityNotFoundException::new);
        ThemeAnalysis themeAnalysis2 = themeAnalysisRepository.findByThemeAndGenre(theme, genre2).orElseThrow(EntityNotFoundException::new);

        Assertions.assertEquals(2, themeAnalysis1.getEvaluatedCount(), "같은 장르로 2번 평가되었기 때문에 평가된 횟수는 2가 나와야 한다.");
        Assertions.assertEquals(2, themeAnalysis2.getEvaluatedCount(),"같은 장르로 2번 평가되었기 때문에 평가된 횟수는 2가 나와야 한다.");

        //final
        themeAnalysisRepository.delete(themeAnalysis1);
        themeAnalysisRepository.delete(themeAnalysis2);
        genreRepository.delete(savedGenre1);
        genreRepository.delete(savedGenre2);
        themeRepository.delete(savedTheme);
    }

    @Test
    @DisplayName("테마 분석 반영 - 기존에 플레이하지 않았던 장르인 경우")
    public void reflectingThemeAnalyses_NotPlayGenres() {
        //given
        Theme theme = Theme.builder()
                .name("theme")
                .build();
        Theme savedTheme = themeRepository.save(theme);

        Genre genre1 = Genre.builder()
                .code("rtagr1")
                .name("genre1")
                .build();

        Genre genre2 = Genre.builder()
                .code("rtagr2")
                .name("genre2")
                .build();

        Genre savedGenre1 = genreRepository.save(genre1);
        Genre savedGenre2 = genreRepository.save(genre2);

        List<Genre> genres = List.of(savedGenre1, savedGenre2);

        //when
        themeAnalysisService.reflectingThemeAnalyses(theme, genres);

        //then
        ThemeAnalysis themeAnalysis1 = themeAnalysisRepository.findByThemeAndGenre(theme, genre1).orElseThrow(EntityNotFoundException::new);
        ThemeAnalysis themeAnalysis2 = themeAnalysisRepository.findByThemeAndGenre(theme, genre2).orElseThrow(EntityNotFoundException::new);

        Assertions.assertEquals(1, themeAnalysis1.getEvaluatedCount(), "같은 장르로 1번 평가되었기 때문에 평가된 횟수는 1가 나와야 한다.");
        Assertions.assertEquals(1, themeAnalysis2.getEvaluatedCount(),"같은 장르로 1번 평가되었기 때문에 평가된 횟수는 1가 나와야 한다.");

        //final
        themeAnalysisRepository.delete(themeAnalysis1);
        themeAnalysisRepository.delete(themeAnalysis2);
        genreRepository.delete(savedGenre1);
        genreRepository.delete(savedGenre2);
        themeRepository.delete(savedTheme);

    }

}