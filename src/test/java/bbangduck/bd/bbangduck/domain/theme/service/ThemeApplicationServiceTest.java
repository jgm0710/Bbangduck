package bbangduck.bd.bbangduck.domain.theme.service;

import bbangduck.bd.bbangduck.domain.genre.entity.Genre;
import bbangduck.bd.bbangduck.domain.model.emumerate.Activity;
import bbangduck.bd.bbangduck.domain.model.emumerate.Difficulty;
import bbangduck.bd.bbangduck.domain.model.emumerate.HorrorGrade;
import bbangduck.bd.bbangduck.domain.model.emumerate.NumberOfPeople;
import bbangduck.bd.bbangduck.domain.shop.entity.Area;
import bbangduck.bd.bbangduck.domain.shop.entity.Franchise;
import bbangduck.bd.bbangduck.domain.shop.entity.Shop;
import bbangduck.bd.bbangduck.domain.theme.dto.controller.response.*;
import bbangduck.bd.bbangduck.domain.theme.entity.Theme;
import bbangduck.bd.bbangduck.domain.theme.entity.ThemeAnalysis;
import bbangduck.bd.bbangduck.domain.theme.entity.ThemeImage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

class ThemeApplicationServiceTest {

    ThemeApplicationService themeMockApplicationService;

    ThemeService themeMockService = mock(ThemeService.class);

    ThemeAnalysisService themeAnalysisMockService = mock(ThemeAnalysisService.class);

    @BeforeEach
    public void setThemeApplicationService() {
        themeMockApplicationService = new ThemeApplicationService(themeMockService, themeAnalysisMockService);
    }

    @Test
    @DisplayName("테마 조회")
    public void getTheme() {
        //given
        Area area = Area.builder()
                .id(1L)
                .code("AR1")
                .name("지역1")
                .build();

        Franchise franchise = Franchise.builder()
                .id(1L)
                .name("franchiseName")
                .build();

        Shop shop = Shop.builder()
                .id(1L)
                .name("shopName")
                .franchise(franchise)
                .area(area)
                .build();

        Theme theme = Theme.builder()
                .id(1L)
                .name("themeName")
                .description("theme description")
                .playTime(LocalTime.now())
                .numberOfPeoples(List.of(NumberOfPeople.ONE, NumberOfPeople.TWO))
                .difficulty(Difficulty.NORMAL)
                .activity(Activity.NORMAL)
                .horrorGrade(HorrorGrade.NORMAL)
                .totalRating(60L)
                .totalEvaluatedCount(16L)
                .shop(shop)
                .build();

        ThemeImage themeImage = ThemeImage.builder()
                .id(1L)
                .fileStorageId(132173L)
                .fileName(UUID.randomUUID() + "fileName")
                .build();

        theme.setThemeImage(themeImage);

        given(themeMockService.getTheme(theme.getId())).willReturn(theme);


        //when
        ThemeDetailResponseDto themeDetailResponseDto = themeMockApplicationService.getTheme(theme.getId());

        //then
        assertEquals(theme.getId(), themeDetailResponseDto.getThemeId());
        assertEquals(theme.getName(), themeDetailResponseDto.getThemeName());
        assertEquals(theme.getDescription(), themeDetailResponseDto.getThemeDescription());
        assertEquals(60f / 16f, themeDetailResponseDto.getThemeRating());
        assertEquals(theme.getPlayTime(), themeDetailResponseDto.getPlayTime());
        assertEquals(theme.getNumberOfPeoples(), themeDetailResponseDto.getNumberOfPeoples());
        assertEquals(theme.getDifficulty(), themeDetailResponseDto.getDifficulty());
        assertEquals(theme.getActivity(), themeDetailResponseDto.getActivity());
        assertEquals(theme.getHorrorGrade(), themeDetailResponseDto.getHorrorGrade());

        ThemeImageResponseDto findThemeImage = themeDetailResponseDto.getThemeImage();
        assertEquals(themeImage.getId(), findThemeImage.getThemeImageId());
        assertTrue(findThemeImage.getThemeImageUrl().contains(themeImage.getFileName()));
        assertTrue(findThemeImage.getThemeImageThumbnailUrl().contains(themeImage.getFileName()));

        ThemeShopSimpleInfoResponseDto shopInfo = themeDetailResponseDto.getShopInfo();
        assertEquals(shop.getId(), shopInfo.getShopId());
        assertEquals(shop.getName(), shopInfo.getShopName());


        ThemeShopFranchiseSimpleInfoResponseDto franchiseInfo = shopInfo.getFranchiseInfo();
        assertEquals(franchise.getId(), franchiseInfo.getFranchiseId());
        assertEquals(franchise.getName(), franchiseInfo.getFranchiseName());

        ThemeShopAreaResponseDto areaInfo = shopInfo.getAreaInfo();
        assertEquals(area.getId(), areaInfo.getAreaId());
        assertEquals(area.getCode(), areaInfo.getAreaCode());
        assertEquals(area.getName(), areaInfo.getAreaName());

    }

    @Test
    @DisplayName("테마 분석 조회")
    public void getThemeAnalyses() {
        //given
        List<ThemeAnalysis> themeAnalyses = new ArrayList<>();

        for (long i = 0; i < 5; i++) {
            Genre genre = Genre.builder()
                    .id(i)
                    .code("GR" + i)
                    .name("genreName" + i)
                    .build();

            ThemeAnalysis themeAnalysis = ThemeAnalysis.builder()
                    .genre(genre)
                    .evaluatedCount(i)
                    .build();
            themeAnalyses.add(themeAnalysis);
        }

        Long themeId = 1L;
        given(themeAnalysisMockService.getThemeAnalyses(themeId)).willReturn(themeAnalyses);

        //when
        List<ThemeAnalysesResponseDto> themeAnalysesResponseDtos = themeMockApplicationService.getThemeAnalyses(themeId);

        //then
        themeAnalysesResponseDtos.forEach(
                themeAnalysesResponseDto -> {
                    assertNotNull(themeAnalysesResponseDto.getEvaluatedCount());

                    ThemeGenreResponseDto themeGenreResponseDto = themeAnalysesResponseDto.getGenre();
                    assertNotNull(themeGenreResponseDto.getGenreId());
                    assertNotNull(themeGenreResponseDto.getGenreCode());
                    assertNotNull(themeGenreResponseDto.getGenreName());

                }
        );

    }

}