package bbangduck.bd.bbangduck.domain.theme.controller;

import bbangduck.bd.bbangduck.domain.theme.dto.controller.request.ThemeGetListRequestDto;
import bbangduck.bd.bbangduck.domain.theme.dto.controller.request.ThemeGetPlayMemberListRequestDto;
import bbangduck.bd.bbangduck.domain.theme.dto.controller.response.ThemeAnalysesResponseDto;
import bbangduck.bd.bbangduck.domain.theme.dto.controller.response.ThemeDetailResponseDto;
import bbangduck.bd.bbangduck.domain.theme.dto.controller.response.ThemeGetListResponseDto;
import bbangduck.bd.bbangduck.domain.theme.dto.controller.response.ThemePlayMemberSimpleInfoResponseDto;
import bbangduck.bd.bbangduck.domain.theme.dto.service.ThemePlayMemberListResultDto;
import bbangduck.bd.bbangduck.domain.theme.entity.Theme;
import bbangduck.bd.bbangduck.domain.theme.entity.ThemeAnalysis;
import bbangduck.bd.bbangduck.domain.theme.service.ThemeApplicationService;
import bbangduck.bd.bbangduck.global.common.CriteriaDto;
import bbangduck.bd.bbangduck.global.common.PaginationResultDto;
import bbangduck.bd.bbangduck.global.common.PaginationResultResponseDto;
import bbangduck.bd.bbangduck.global.common.ResponseStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

import static bbangduck.bd.bbangduck.global.common.ThrowUtils.hasErrorsThrow;

/**
 * 테마와 관련된 EndPoint 를 구현하기 위한 Api Controller
 *
 * @author jgm
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/themes")
@Slf4j
public class ThemeApiController {

    private final ThemeApplicationService themeApplicationService;

    @GetMapping
    @org.springframework.web.bind.annotation.ResponseStatus(HttpStatus.OK)
    public PaginationResultResponseDto<ThemeGetListResponseDto> getThemeList(
            @ModelAttribute @Valid CriteriaDto criteriaDto,
            BindingResult bindingResult,
            @ModelAttribute @Valid ThemeGetListRequestDto requestDto
    ) {
        hasErrorsThrow(ResponseStatus.GET_THEME_LIST_NOT_VALID, bindingResult);

        PaginationResultDto<Theme> result = themeApplicationService.getThemeList(criteriaDto, requestDto.toServiceDto());

        return new PaginationResultResponseDto<>(
                result.getContents(),
                criteriaDto.getPageNum(),
                criteriaDto.getAmount(),
                result.getTotalResultsCount())
                .convert(ThemeGetListResponseDto::convert);
    }

    @GetMapping("/{themeId}")
    @org.springframework.web.bind.annotation.ResponseStatus(HttpStatus.OK)
    public ThemeDetailResponseDto getTheme(
            @PathVariable Long themeId
    ) {
        Theme theme = themeApplicationService.getTheme(themeId);
        return ThemeDetailResponseDto.convert(theme);
    }

    @GetMapping("/{themeId}/analyses")
    @org.springframework.web.bind.annotation.ResponseStatus(HttpStatus.OK)
    public List<ThemeAnalysesResponseDto> getThemeAnalyses(
            @PathVariable Long themeId
    ) {
        List<ThemeAnalysis> themeAnalyses = themeApplicationService.getThemeAnalyses(themeId);
        return themeAnalyses.stream().map(ThemeAnalysesResponseDto::convert).collect(Collectors.toList());
    }

    @GetMapping("{themeId}/members")
    @org.springframework.web.bind.annotation.ResponseStatus(HttpStatus.OK)
    public PaginationResultResponseDto<ThemePlayMemberSimpleInfoResponseDto> getThemePlayMemberList(
            @PathVariable Long themeId,
            @Valid ThemeGetPlayMemberListRequestDto requestDto,
            BindingResult bindingResult
    ) {
        hasErrorsThrow(ResponseStatus.GET_THEME_PLAY_MEMBER_LIST_NOT_VALID, bindingResult);

        ThemePlayMemberListResultDto themePlayMemberListResultDto = themeApplicationService.getThemePlayMemberList(themeId, requestDto.toServiceDto());

        return new PaginationResultResponseDto<>(
                themePlayMemberListResultDto.getMembers(),
                requestDto.getPageNum(),
                requestDto.getAmount(),
                themePlayMemberListResultDto.getThemePlayMembersCount()
        ).convert(ThemePlayMemberSimpleInfoResponseDto::convert);
    }

    // TODO: 2021-06-28 테마 검색 구현
}
