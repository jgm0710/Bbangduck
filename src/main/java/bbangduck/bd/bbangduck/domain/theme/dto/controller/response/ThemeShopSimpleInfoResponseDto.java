package bbangduck.bd.bbangduck.domain.theme.dto.controller.response;

import bbangduck.bd.bbangduck.domain.shop.entity.Shop;
import bbangduck.bd.bbangduck.global.common.NullCheckUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 테마 상세 조회 시 테마의 샵에 대한 응답 Data 를 담을 Dto
 *
 * @author jgm
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ThemeShopSimpleInfoResponseDto {

    private ThemeShopFranchiseSimpleInfoResponseDto franchiseInfo;

    private Long shopId;

    private String shopName;

    private ThemeShopAreaResponseDto areaInfo;

    public static ThemeShopSimpleInfoResponseDto convert(Shop shop) {
        if (!NullCheckUtils.isNotNull(shop)) {
            return null;
        }

        return ThemeShopSimpleInfoResponseDto.builder()
                .franchiseInfo(ThemeShopFranchiseSimpleInfoResponseDto.convert(shop.getFranchise()))
                .shopId(shop.getId())
                .shopName(shop.getName())
                .areaInfo(ThemeShopAreaResponseDto.convert(shop.getArea()))
                .build();
    }
}
