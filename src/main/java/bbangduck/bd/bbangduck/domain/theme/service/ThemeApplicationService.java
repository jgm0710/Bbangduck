package bbangduck.bd.bbangduck.domain.theme.service;

import bbangduck.bd.bbangduck.domain.member.entity.Member;
import bbangduck.bd.bbangduck.domain.theme.dto.controller.response.*;
import bbangduck.bd.bbangduck.domain.theme.dto.service.ThemeGetListDto;
import bbangduck.bd.bbangduck.domain.theme.dto.service.ThemeGetPlayMemberListDto;
import bbangduck.bd.bbangduck.domain.theme.entity.Theme;
import bbangduck.bd.bbangduck.domain.theme.entity.ThemeAnalysis;
import bbangduck.bd.bbangduck.global.common.CriteriaDto;
import bbangduck.bd.bbangduck.global.common.PaginationResultResponseDto;
import com.querydsl.core.QueryResults;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    private final ThemeAnalysisService themeAnalysisService;

    public PaginationResultResponseDto<ThemeGetListResponseDto> getThemeList(CriteriaDto criteriaDto, ThemeGetListDto themeGetListDto) {
        QueryResults<Theme> themeQueryResults = themeService.getThemeList(criteriaDto, themeGetListDto);

        long totalResultsCount = themeQueryResults.getTotal();
        List<Theme> themes = themeQueryResults.getResults();

        return new PaginationResultResponseDto<>(themes,
                criteriaDto.getPageNum(),
                criteriaDto.getAmount(),
                totalResultsCount).convert(ThemeGetListResponseDto::convert);
    }

    public ThemeDetailResponseDto getTheme(Long themeId) {
        Theme theme = themeService.getTheme(themeId);
        return ThemeDetailResponseDto.convert(theme);
    }

    public List<ThemeAnalysesResponseDto> getThemeAnalyses(Long themeId) {
        themeService.getTheme(themeId);
        List<ThemeAnalysis> themeAnalyses = themeAnalysisService.getThemeAnalyses(themeId);
        return themeAnalyses.stream().map(ThemeAnalysesResponseDto::convert).collect(Collectors.toList());
    }

    public ThemePlayMemberListResponseDto getThemePlayMemberList(Long themeId, ThemeGetPlayMemberListDto themeGetPlayMemberListDto) {
        themeService.getTheme(themeId);
        List<Member> themePlayMemberList = themeService.findThemePlayMemberList(themeId, themeGetPlayMemberListDto);
        long themePlayMembersCount = themeService.getThemePlayMembersCount(themeId);

        return ThemePlayMemberListResponseDto.convert(themePlayMemberList,
                themeGetPlayMemberListDto.getAmount(),
                themePlayMembersCount);
    }
}
