package bbangduck.bd.bbangduck.domain.theme.service;

import bbangduck.bd.bbangduck.domain.member.entity.Member;
import bbangduck.bd.bbangduck.domain.theme.dto.service.ThemeGetListDto;
import bbangduck.bd.bbangduck.domain.theme.dto.service.ThemeGetPlayMemberListDto;
import bbangduck.bd.bbangduck.domain.theme.dto.service.ThemePlayMemberListResultDto;
import bbangduck.bd.bbangduck.domain.theme.entity.Theme;
import bbangduck.bd.bbangduck.domain.theme.entity.ThemeAnalysis;
import bbangduck.bd.bbangduck.domain.theme.entity.ThemePlayMember;
import bbangduck.bd.bbangduck.global.common.CriteriaDto;
import bbangduck.bd.bbangduck.global.common.PaginationResultDto;
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
@RequiredArgsConstructor
public class ThemeApplicationService {

    private final ThemeService themeService;

    private final ThemeAnalysisService themeAnalysisService;

    private final ThemePlayMemberService themePlayMemberService;

    @Transactional(readOnly = true)
    public PaginationResultDto<Theme> getThemeList(ThemeGetListDto themeGetListDto) {
        QueryResults<Theme> themeQueryResults = themeService.getThemeList(themeGetListDto);

        long totalResultsCount = themeQueryResults.getTotal();
        List<Theme> themes = themeQueryResults.getResults();

        return new PaginationResultDto<>(themes, totalResultsCount);
    }

    @Transactional(readOnly = true)
    public Theme getTheme(Long themeId) {
        return themeService.getTheme(themeId);
    }

    @Transactional(readOnly = true)
    public List<ThemeAnalysis> getThemeAnalyses(Long themeId) {
        themeService.getTheme(themeId);
        return themeAnalysisService.getThemeAnalyses(themeId);
    }

    @Transactional(readOnly = true)
    public ThemePlayMemberListResultDto getThemePlayMemberList(Long themeId, ThemeGetPlayMemberListDto themeGetPlayMemberListDto) {
        themeService.getTheme(themeId);
        List<ThemePlayMember> themePlayMemberEntities = themePlayMemberService.findThemePlayMemberList(themeId, themeGetPlayMemberListDto);
        List<Member> themePlayMembers = themePlayMemberEntities.stream().map(ThemePlayMember::getMember).collect(Collectors.toList());
        long themePlayMembersCount = themePlayMemberService.getThemePlayMembersCount(themeId);

        return ThemePlayMemberListResultDto.builder()
                .members(themePlayMembers)
                .themePlayMembersCount(themePlayMembersCount)
                .build();
    }
}
