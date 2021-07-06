package bbangduck.bd.bbangduck.domain.theme.service;

import bbangduck.bd.bbangduck.domain.theme.entity.ThemeAnalysis;
import bbangduck.bd.bbangduck.domain.theme.repository.ThemeAnalysisQueryRepository;
import bbangduck.bd.bbangduck.domain.theme.repository.ThemeAnalysisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 테마 분석과 관련된 비즈니스 로직을 구현하기 위한 Service
 *
 * @author jgm
 */
@Service
@RequiredArgsConstructor
public class ThemeAnalysisService {

    private final ThemeAnalysisRepository themeAnalysisRepository;

    private final ThemeAnalysisQueryRepository themeAnalysisQueryRepository;

    public List<ThemeAnalysis> getThemeAnalyses(Long themeId) {
        return themeAnalysisQueryRepository.findByThemeId(themeId);
    }
}
