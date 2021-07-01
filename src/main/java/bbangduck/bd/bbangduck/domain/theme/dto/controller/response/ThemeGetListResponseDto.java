package bbangduck.bd.bbangduck.domain.theme.dto.controller.response;

import bbangduck.bd.bbangduck.domain.shop.entity.Franchise;
import bbangduck.bd.bbangduck.domain.shop.entity.Shop;
import bbangduck.bd.bbangduck.domain.theme.entity.Theme;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 테마 목록 조회 API 의 응답 Body Data 를 담을 Dto
 *
 * @author jgm
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ThemeGetListResponseDto {

    private Long themeId;

    private ThemeImageResponseDto themeImage;

    private String themeName;

    private String franchiseName;

    private String shopName;

    public static ThemeGetListResponseDto convert(Theme theme) {
        Shop themeShop = theme.getShop();
        Franchise themeShopFranchise = themeShop.getFranchise();

        return ThemeGetListResponseDto.builder()
                .themeId(theme.getId())
                .themeImage(ThemeImageResponseDto.convert(theme.getThemeImage()))
                .themeName(theme.getName())
                .franchiseName(themeShopFranchise.getName())
                .shopName(themeShop.getName())
                .build();
    }

}
