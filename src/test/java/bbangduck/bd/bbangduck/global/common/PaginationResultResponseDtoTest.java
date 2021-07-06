package bbangduck.bd.bbangduck.global.common;

import bbangduck.bd.bbangduck.domain.shop.entity.Area;
import bbangduck.bd.bbangduck.domain.shop.entity.Franchise;
import bbangduck.bd.bbangduck.domain.shop.entity.Shop;
import bbangduck.bd.bbangduck.domain.theme.dto.controller.response.ThemeGetListResponseDto;
import bbangduck.bd.bbangduck.domain.theme.entity.Theme;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

class PaginationResultResponseDtoTest {

    @Test
    @DisplayName("페이징 처리 Dto 테스트")
    void paginationResultResponseDtoTest() {
        //given
        List<Theme> themeList = new ArrayList<>();
        for (long i = 1; i < 11; i++) {
            Area area = Area.builder()
                    .id(i)
                    .code("CD" + i)
                    .name("areaName" + i)
                    .build();

            Franchise franchise = Franchise.builder()
                    .id(i)
                    .name("franchiseName" + i)
                    .build();

            Shop shop = Shop.builder()
                    .id(i)
                    .name("shopName" + i)
                    .area(area)
                    .franchise(franchise)
                    .build();

            Theme theme = Theme.builder()
                    .id(i)
                    .shop(shop)
                    .name("themeName" + i)
                    .build();
            themeList.add(theme);
        }

        //when
        PaginationResultResponseDto<Theme> themePaginationResultResponseDto = new PaginationResultResponseDto<>(themeList, 1, 10, 10);
        PaginationResultResponseDto<ThemeGetListResponseDto> results = themePaginationResultResponseDto.convert(ThemeGetListResponseDto::convert);

        //then
        System.out.println("results = " + results);

    }

    @Test
    @DisplayName("null Contents Test")
    public void nullContentsTest() throws Exception {
        //given
        List<Theme> themes = new ArrayList<>();
        PaginationResultResponseDto<ThemeGetListResponseDto> result = new PaginationResultResponseDto<>(themes, 1, 1, 1).convert(ThemeGetListResponseDto::convert);

        //when
        System.out.println("result = " + result);


        //then

    }
}