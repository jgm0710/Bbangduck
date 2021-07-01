package bbangduck.bd.bbangduck.domain.theme.service;

import bbangduck.bd.bbangduck.domain.theme.dto.service.ThemeGetListDto;
import bbangduck.bd.bbangduck.domain.theme.entity.Theme;
import bbangduck.bd.bbangduck.domain.theme.repository.ThemeQueryRepository;
import bbangduck.bd.bbangduck.global.common.CriteriaDto;
import com.querydsl.core.QueryResults;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 테마와 관련된 비즈니스 로직을 구현한 Service
 *
 * @author jgm
 */
@Service
@RequiredArgsConstructor
public class ThemeService {

    private final ThemeQueryRepository themeQueryRepository;

    public QueryResults<Theme> getThemeList(CriteriaDto criteriaDto, ThemeGetListDto themeGetListDto) {
        return themeQueryRepository.findList(criteriaDto, themeGetListDto);
    }
}
