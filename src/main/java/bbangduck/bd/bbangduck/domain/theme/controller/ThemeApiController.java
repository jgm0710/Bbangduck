package bbangduck.bd.bbangduck.domain.theme.controller;

import bbangduck.bd.bbangduck.domain.review.dto.controller.response.PaginationResponseDto;
import bbangduck.bd.bbangduck.domain.theme.dto.controller.request.ThemeGetListRequestDto;
import bbangduck.bd.bbangduck.domain.theme.dto.controller.response.ThemeAnalysesResponseDto;
import bbangduck.bd.bbangduck.domain.theme.dto.controller.response.ThemeDetailResponseDto;
import bbangduck.bd.bbangduck.domain.theme.service.ThemeApplicationService;
import bbangduck.bd.bbangduck.global.common.CriteriaDto;
import bbangduck.bd.bbangduck.global.common.ResponseStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import java.util.List;

import static bbangduck.bd.bbangduck.global.common.ThrowUtils.hasErrorsThrow;
import static bbangduck.bd.bbangduck.global.common.util.RequestUtils.getParametersFromRequest;

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
    public ResponseEntity<PaginationResponseDto> getThemeList(
            @ModelAttribute @Valid CriteriaDto criteriaDto,
            BindingResult bindingResult,
            @ModelAttribute @Valid ThemeGetListRequestDto requestDto,
            HttpServletRequest request
    ) {
        hasErrorsThrow(ResponseStatus.GET_THEME_LIST_NOT_VALID, bindingResult);

        PaginationResponseDto themeListPaginationResponseDto = themeApplicationService.getThemeList(criteriaDto,
                requestDto.toServiceDto(),
                request.getRequestURL().toString(),
                getParametersFromRequest(request));

        return ResponseEntity.ok(themeListPaginationResponseDto);
    }

    @GetMapping("/{themeId}")
    public ResponseEntity<ThemeDetailResponseDto> getTheme(
            @PathVariable Long themeId
    ) {
        ThemeDetailResponseDto themeDetailResponseDto = themeApplicationService.getTheme(themeId);
        return ResponseEntity.ok(themeDetailResponseDto);
    }

    @GetMapping("/{themeId}/analyses")
    public ResponseEntity<List<ThemeAnalysesResponseDto>> getThemeAnalyses(
            @PathVariable Long themeId
    ) {
        List<ThemeAnalysesResponseDto> themeAnalyses = themeApplicationService.getThemeAnalyses(themeId);
        return ResponseEntity.ok(themeAnalyses);
    }

    // TODO: 2021-06-28 테마 검색 구현
}
