package bbangduck.bd.bbangduck.domain.shop.dto.controller;


import bbangduck.bd.bbangduck.domain.shop.entity.Shop;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PRIVATE)
public class ShopResponseDto {
  private final Long shopId;
  private final String shopImageUrl;
  private final String shopUrl;
  private final String description;
  private final String address;

  public static ShopResponseDto by(Shop shop) {
    return ShopResponseDto.builder()
        .shopId(shop.getId())
        .shopImageUrl(shop.getImageUrl())
        .shopUrl(shop.getUrl())
        .description(shop.getDescription())
        .address(shop.getAddress())
        .build();
  }

}
