package bbangduck.bd.bbangduck.domain.theme.service;

import bbangduck.bd.bbangduck.domain.theme.entity.Theme;
import bbangduck.bd.bbangduck.domain.theme.repository.ThemeQueryRepository;
import bbangduck.bd.bbangduck.domain.theme.repository.ThemeRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("ThemeService 단위 테스트")
class ThemeServiceUnitTest {

    ThemeRepository themeRepository = Mockito.mock(ThemeRepository.class);
    ThemeQueryRepository themeQueryRepository = Mockito.mock(ThemeQueryRepository.class);

    ThemeService themeService = new ThemeService(themeRepository, themeQueryRepository);

    @Test
    @DisplayName("테마 레이팅 증가")
    public void increaseThemeRating() {
        //given
        long firstRating = 10L;
        long firstEvaluateCount = 2L;

        Theme theme = Theme.builder()
                .id(1L)
                .totalRating(firstRating)
                .totalEvaluatedCount(firstEvaluateCount)
                .build();

        //when
        int increaseRating = 5;
        themeService.increaseThemeRating(theme, increaseRating);

        //then
        assertEquals(firstRating + increaseRating, theme.getTotalRating());
        assertEquals(firstEvaluateCount + 1, theme.getTotalEvaluatedCount());
    }

    @Test
    @DisplayName("테마 레이팅 감소")
    public void decreaseThemeRating() {
        //given
        long firstRating = 10L;
        long firstEvaluateCount = 2L;

        Theme theme = Theme.builder()
                .id(1L)
                .totalRating(firstRating)
                .totalEvaluatedCount(firstEvaluateCount)
                .build();

        //when
        int decreaseRating = 3;
        themeService.decreaseThemeRating(theme, decreaseRating);

        //then
        assertEquals(firstRating-decreaseRating, theme.getTotalRating());
        assertEquals(firstEvaluateCount - 1, theme.getTotalEvaluatedCount());

    }

    @Test
    @DisplayName("테마 레이팅 업데이트")
    public void updateThemeRating() {
        //given
        long firstRating = 10L;
        long firstEvaluateCount = 2L;

        Theme theme = Theme.builder()
                .id(1L)
                .totalRating(firstRating)
                .totalEvaluatedCount(firstEvaluateCount)
                .build();

        //when
        int decreaseRating = 4;
        int increaseRating = 3;
        themeService.updateThemeRating(theme, decreaseRating, increaseRating);

        //then
        assertEquals(firstRating-decreaseRating+increaseRating, theme.getTotalRating());
        assertEquals(firstEvaluateCount, theme.getTotalEvaluatedCount());
    }

}