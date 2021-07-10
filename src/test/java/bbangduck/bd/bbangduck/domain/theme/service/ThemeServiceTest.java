package bbangduck.bd.bbangduck.domain.theme.service;

import bbangduck.bd.bbangduck.domain.theme.entity.Theme;
import bbangduck.bd.bbangduck.domain.theme.exception.ManipulateDeletedThemeException;
import bbangduck.bd.bbangduck.domain.theme.exception.ThemeNotFoundException;
import bbangduck.bd.bbangduck.domain.theme.repository.ThemeQueryRepository;
import bbangduck.bd.bbangduck.domain.theme.repository.ThemeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

class ThemeServiceTest {

    ThemeService themeMockService;

    ThemeRepository themeMockRepository = mock(ThemeRepository.class);

    ThemeQueryRepository themeMockQueryRepository = mock(ThemeQueryRepository.class);

    @BeforeEach
    public void themeServiceSetUp() {
        themeMockService = new ThemeService(
                themeMockRepository,
                themeMockQueryRepository
        );
    }


    @Test
    @DisplayName("테마 조회 - 테마를 찾을 수 없는 경우")
    public void getTheme_NotFound() {
        //given
        given(themeMockRepository.findById(1L)).willReturn(Optional.empty());

        //when

        //then
        assertThrows(ThemeNotFoundException.class, () -> themeMockService.getTheme(1L));

    }

    @Test
    @DisplayName("테마 조회 - 삭제된 테마인 경우")
    public void getTheme_DeletedTheme() {
        //given
        Theme theme = Theme.builder()
                .id(1L)
                .deleteYN(true)
                .build();

        given(themeMockRepository.findById(1L)).willReturn(Optional.ofNullable(theme));

        //when

        //then
        assertThrows(ManipulateDeletedThemeException.class, () -> themeMockService.getTheme(1L));

    }

    @Test
    @DisplayName("리뷰 생성 기입한 평점 테마에 반영")
    public void reflectThemeRating() {
        //given
        Theme theme = Theme.builder()
                .id(1L)
                .totalRating(0L)
                .totalEvaluatedCount(0L)
                .deleteYN(false)
                .build();

        //when
        themeMockService.increaseThemeRating(theme, 5);

        //then
        assertEquals(5, theme.getTotalRating());
        assertEquals(1, theme.getTotalEvaluatedCount());

    }

}