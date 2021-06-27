package bbangduck.bd.bbangduck.domain.shop.dto.service;

import bbangduck.bd.bbangduck.domain.shop.entity.Franchise;
import lombok.Builder;
import lombok.Getter;

@Getter
public class ShopCreateCommand {
  private final double latitude;
  private final double longitude;
  private final Long fileStorageId;
  private final String fileName;
  private final Franchise franchise;
  private final String description;
  private final String name;
  private final String address;
  private final String shopUrl;

  @Builder
  public ShopCreateCommand(double latitude, double longitude, Long fileStorageId, String fileName, Franchise franchise, String description, String name, String address, String shopUrl) {
    this.latitude = latitude;
    this.longitude = longitude;
    this.fileStorageId = fileStorageId;
    this.fileName = fileName;
    this.franchise = franchise;
    this.description = description;
    this.name = name;
    this.address = address;
    this.shopUrl = shopUrl;
  }
}
