package bbangduck.bd.bbangduck.domain.theme.service;

import bbangduck.bd.bbangduck.common.BaseTest;
import bbangduck.bd.bbangduck.domain.theme.entity.Theme;
import bbangduck.bd.bbangduck.domain.theme.repository.ThemeRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class ThemeDeveloperApplicationServiceTest extends BaseTest {

    @Autowired
    ThemeDeveloperApplicationService themeDeveloperApplicationService;

    @Autowired
    ThemeRepository themeRepository;

    @Autowired
    ThemeApplicationService themeApplicationService;

    @Test
    @DisplayName("sample")
    public void sample() {
        //given


        Theme theme = Theme.builder()
                .name("theme")
                .build();
        Theme saved = themeRepository.save(theme);


        //when
        themeDeveloperApplicationService.addImageToTheme(saved.getId(), 1L, "fileName");

        //then
        Theme findTheme = themeApplicationService.getTheme(saved.getId());

        System.out.println("findTheme = " + findTheme);


    }

}