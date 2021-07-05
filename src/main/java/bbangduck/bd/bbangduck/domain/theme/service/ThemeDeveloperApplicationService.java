package bbangduck.bd.bbangduck.domain.theme.service;

import bbangduck.bd.bbangduck.domain.theme.entity.Theme;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 개발자 권한을 통한 테마 리소스 조작 비즈니스 로직을 구현한 Application Service
 *
 * @author jgm
 */
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ThemeDeveloperApplicationService {

    private final ThemeService themeService;

    private final ThemeDeveloperService themeDeveloperService;


    @Transactional
    public void addImageToTheme(Long themeId, Long fileStorageId, String fileName) {
        Theme theme = themeService.getTheme(themeId);
        themeDeveloperService.addImageToTheme(theme, fileStorageId, fileName);
    }
}
