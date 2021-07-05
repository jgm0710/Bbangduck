package bbangduck.bd.bbangduck.domain.theme.controller;

import bbangduck.bd.bbangduck.domain.theme.dto.controller.request.ThemeImageRequestDto;
import bbangduck.bd.bbangduck.domain.theme.service.ThemeDeveloperApplicationService;
import bbangduck.bd.bbangduck.global.common.ResponseStatus;
import bbangduck.bd.bbangduck.global.common.ThrowUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import java.net.URI;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * 개발자 권한을 통해 테마 리소스에 접근이 가능한 EndPoint 를 구현한 Controller
 *
 * @author jgm
 */
@RestController
@RequestMapping("/api/develop/themes")
@RequiredArgsConstructor
public class ThemeDeveloperApiController {

    private final ThemeDeveloperApplicationService themeDeveloperApplicationService;

    @PostMapping("/{themeId}/images")
    @PreAuthorize("hasRole('ROLE_DEVELOP')")
    public ResponseEntity<Object> addImageToTheme(
            @PathVariable Long themeId,
            @RequestBody @Valid ThemeImageRequestDto requestDto,
            Errors errors
    ) {
        ThrowUtils.hasErrorsThrow(ResponseStatus.ADD_IMAGE_TO_THEME_BY_DEVELOPER_NOT_VALID, errors);
        themeDeveloperApplicationService.addImageToTheme(themeId, requestDto.getFileStorageId(), requestDto.getFileName());

        URI linkToGetTheme = linkTo(methodOn(ThemeApiController.class).getTheme(themeId)).toUri();

        return ResponseEntity.created(linkToGetTheme).build();
    }
}
