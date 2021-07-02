package bbangduck.bd.bbangduck.domain.theme.dto.controller.response;

import bbangduck.bd.bbangduck.domain.shop.entity.Franchise;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 테마 상세 조회 시 테마의 샵의 프렌차이즈에 대한 간단한 응답 Data 를 담을 Dto
 *
 * @author jgm
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ThemeShopFranchiseSimpleInfoResponseDto {

    private Long franchiseId;

    private String franchiseName;

    public static ThemeShopFranchiseSimpleInfoResponseDto convert(Franchise franchise) {
        return ThemeShopFranchiseSimpleInfoResponseDto.builder()
                .franchiseId(franchise.getId())
                .franchiseName(franchise.getName())
                .build();
    }
}
