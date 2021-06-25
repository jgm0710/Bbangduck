package bbangduck.bd.bbangduck.domain.shop.dto.controller;

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
}
