package bbangduck.bd.bbangduck.domain.shop.dto.controller;

import bbangduck.bd.bbangduck.domain.shop.entity.Shop;
import bbangduck.bd.bbangduck.domain.shop.entity.embeded.Location;
import bbangduck.bd.bbangduck.global.common.util.DistanceUtil;
import lombok.Getter;

public class ShopLocationResponseDto {
  @Getter
  private final double distance;
  @Getter
  private final String title;
  @Getter
  private final double latitude;
  @Getter
  private final double longitude;

  public ShopLocationResponseDto(String title, double distance, double latitude, double longitude) {
    this.distance = distance;
    this.title = title;
    this.latitude = latitude;
    this.longitude = longitude;
  }

  public static ShopLocationResponseDto byWith(Shop shop, Location location) {
    return new ShopLocationResponseDto(
        shop.getName(),
        DistanceUtil.getDistance(location, shop.getLocation()),
        shop.getLatitude(),
        shop.getLongitude()
    );
  }
}
