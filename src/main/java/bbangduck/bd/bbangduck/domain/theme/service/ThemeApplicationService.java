package bbangduck.bd.bbangduck.domain.theme.service;

import bbangduck.bd.bbangduck.domain.review.dto.controller.response.PaginationResponseDto;
import bbangduck.bd.bbangduck.domain.theme.dto.controller.response.ThemeDetailResponseDto;
import bbangduck.bd.bbangduck.domain.theme.dto.controller.response.ThemeGetListResponseDto;
import bbangduck.bd.bbangduck.domain.theme.dto.service.ThemeGetListDto;
import bbangduck.bd.bbangduck.domain.theme.entity.Theme;
import bbangduck.bd.bbangduck.global.common.CriteriaDto;
import com.querydsl.core.QueryResults;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MultiValueMap;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 테마 관련 여러 Service 로직을 취합하여 Api 에서 사용할 Service 로직을 구현하기 위한 Application Service
 *
 * @author jgm
 */
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ThemeApplicationService {

    private final ThemeService themeService;

    public PaginationResponseDto getThemeList(CriteriaDto criteriaDto, ThemeGetListDto themeGetListDto, String requestPath, MultiValueMap<String, String> params) {
        QueryResults<Theme> themeQueryResults = themeService.getThemeList(criteriaDto, themeGetListDto);

        long totalResultsCount = themeQueryResults.getTotal();
        List<Theme> themes = themeQueryResults.getResults();

        List<ThemeGetListResponseDto> themeGetListResponseDtos = themes.stream().map(ThemeGetListResponseDto::convert).collect(Collectors.toList());

        return PaginationResponseDto.convert(themeGetListResponseDtos, criteriaDto, totalResultsCount, requestPath, params);
    }

    public ThemeDetailResponseDto getTheme(Long themeId) {
        Theme theme = themeService.getTheme(themeId);
        return ThemeDetailResponseDto.convert(theme);
    }
}
