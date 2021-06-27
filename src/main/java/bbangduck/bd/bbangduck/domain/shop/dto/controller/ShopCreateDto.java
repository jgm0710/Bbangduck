package bbangduck.bd.bbangduck.domain.shop.dto.controller;

import bbangduck.bd.bbangduck.domain.shop.service.ShopApplicationService;
import lombok.*;

@ToString
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode
public class ShopCreateDto {
  private String name;
  private String shopUrl;
  private String shopInfo;
  private String address;
  private Long franchiseId;
  private Double lon;
  private Double lat;
  private Long fileStorageId;
  private String fileName;

  public ShopApplicationService.CreateDto toCreateDto() {
    return ShopApplicationService.CreateDto.builder()
        .latitude(lat)
        .longitude(lon)
        .franchiseId(franchiseId)
        .description(shopInfo)
        .fileName(fileName)
        .fileStorageId(fileStorageId)
        .name(name)
        .shopUrl(shopUrl)
        .address(address)
        .build();

  }


}
