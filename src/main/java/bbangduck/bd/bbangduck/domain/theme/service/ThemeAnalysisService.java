package bbangduck.bd.bbangduck.domain.theme.service;

import bbangduck.bd.bbangduck.domain.genre.Genre;
import bbangduck.bd.bbangduck.domain.theme.entity.Theme;
import bbangduck.bd.bbangduck.domain.theme.entity.ThemeAnalysis;
import bbangduck.bd.bbangduck.domain.theme.repository.ThemeAnalysisQueryRepository;
import bbangduck.bd.bbangduck.domain.theme.repository.ThemeAnalysisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 테마 분석과 관련된 비즈니스 로직을 구현하기 위한 Service
 *
 * @author jgm
 * @since 2021-07-10
 */
@Service
@RequiredArgsConstructor
public class ThemeAnalysisService {

    private final ThemeAnalysisRepository themeAnalysisRepository;

    private final ThemeAnalysisQueryRepository themeAnalysisQueryRepository;

    public List<ThemeAnalysis> getThemeAnalyses(Long themeId) {
        return themeAnalysisQueryRepository.findByThemeId(themeId);
    }

    @Transactional
    public void reflectingThemeAnalyses(Theme theme, List<Genre> genres) {
        genres.forEach(genre -> {
            ThemeAnalysis themeAnalysis = themeAnalysisRepository.findByThemeAndGenre(theme, genre).orElse(ThemeAnalysis.init(theme, genre));
            themeAnalysis.increaseEvaluatedCount();
            themeAnalysisRepository.save(themeAnalysis);
        });
    }
}
