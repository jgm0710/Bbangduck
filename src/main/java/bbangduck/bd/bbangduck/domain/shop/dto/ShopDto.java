package bbangduck.bd.bbangduck.domain.shop.dto;

import bbangduck.bd.bbangduck.domain.shop.entity.Area;
import bbangduck.bd.bbangduck.domain.shop.entity.Franchise;
import bbangduck.bd.bbangduck.domain.shop.entity.ShopImage;
import bbangduck.bd.bbangduck.domain.shop.entity.embeded.Location;
import lombok.*;

@ToString
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode
public class ShopDto {
    private Long id;
    private String name;
    private String shopUrl;
    private String shopInfo;
    private String address;

    private Long price;
    private Long areaId;
    private Long franchiseId;
    private Long ShopImageId;

    private Double lon;
    private Double lat;



}
