package bbangduck.bd.bbangduck.domain.theme.repository;

import bbangduck.bd.bbangduck.common.BaseTest;
import bbangduck.bd.bbangduck.domain.genre.entity.Genre;
import bbangduck.bd.bbangduck.domain.genre.repository.GenreRepository;
import bbangduck.bd.bbangduck.domain.theme.entity.Theme;
import bbangduck.bd.bbangduck.domain.theme.entity.ThemeAnalysis;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ThemeAnalysisQueryRepositoryTest extends BaseTest {

    @Autowired
    ThemeAnalysisQueryRepository themeAnalysisQueryRepository;

    @Autowired
    ThemeAnalysisRepository themeAnalysisRepository;

    @Autowired
    ThemeRepository themeRepository;

    @Autowired
    GenreRepository genreRepository;

    @Autowired
    EntityManager em;

    @Transactional
    @Test
    @DisplayName("테마 분석 조회")
    public void getThemeAnalysis() {
        //given
        Theme theme1 = Theme.builder()
                .id(1L)
                .build();
        Theme savedTheme1 = themeRepository.save(theme1);

        Theme theme2 = Theme.builder()
                .id(2L)
                .build();
        Theme savedTheme2 = themeRepository.save(theme2);


        for (int i = 0; i < 15; i++) {
            Genre genre = Genre.builder()
                    .code("CD" + i)
                    .name("genre" + i)
                    .build();
            Genre savedGenre = genreRepository.save(genre);

            ThemeAnalysis themeAnalysis = ThemeAnalysis.builder()
                    .theme(savedTheme1)
                    .genre(savedGenre)
                    .evaluatedCount((long) new Random().nextInt(15))
                    .build();
            themeAnalysisRepository.save(themeAnalysis);
        }

        em.flush();
        em.clear();

        //when
        System.out.println("===================================================================================================");
        List<ThemeAnalysis> findThemeAnalyses = themeAnalysisQueryRepository.findByThemeId(theme1.getId());
        System.out.println("===================================================================================================");

        //then
        for (int i = 0; i < findThemeAnalyses.size()-1; i++) {
            ThemeAnalysis nowThemeAnalysis = findThemeAnalyses.get(i);
            ThemeAnalysis nextThemeAnalysis = findThemeAnalyses.get(i + 1);

            assertTrue(nowThemeAnalysis.getEvaluatedCount() >= nextThemeAnalysis.getEvaluatedCount());

            if (nowThemeAnalysis.getEvaluatedCount().equals(nextThemeAnalysis.getEvaluatedCount())) {
                Genre nowThemeAnalysisGenre = nowThemeAnalysis.getGenre();
                Genre nextThemeAnalysisGenre = nextThemeAnalysis.getGenre();

                assertTrue(nowThemeAnalysisGenre.getName().compareTo(nextThemeAnalysisGenre.getName())<0);
            }

            Theme nowThemeAnalysisTheme = nowThemeAnalysis.getTheme();
            assertEquals(theme1.getId(), nowThemeAnalysisTheme.getId());

        }

    }

}