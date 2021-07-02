package bbangduck.bd.bbangduck.domain.theme.dto.controller.response;

import bbangduck.bd.bbangduck.domain.shop.entity.Area;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 테마 상세 조회 시 테마의 샵의 지역에 대한 간단한 응답 Data 를 담을 Dto
 *
 * @author jgm
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ThemeShopAreaResponseDto {

    private Long areaId;

    private String areaCode;

    private String areaName;

    public static ThemeShopAreaResponseDto convert(Area area) {
        return ThemeShopAreaResponseDto.builder()
                .areaId(area.getId())
                .areaCode(area.getCode())
                .areaName(area.getName())
                .build();
    }
}
