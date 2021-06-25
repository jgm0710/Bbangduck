package bbangduck.bd.bbangduck.domain.shop.mapper;

import bbangduck.bd.bbangduck.domain.model.embeded.Location;
import bbangduck.bd.bbangduck.domain.shop.dto.controller.ShopLocationResponseDto;
import bbangduck.bd.bbangduck.domain.shop.entity.Shop;
import bbangduck.bd.bbangduck.global.common.util.DistanceUtil;

public interface ShopToLocationResponseDto {
  static ShopLocationResponseDto convert(Shop shop, Location location) {
    return new ShopLocationResponseDto(
        shop.getName(),
        DistanceUtil.getDistance(location, shop.getLocation()),
        shop.getLatitude(),
        shop.getLongitude()
    );
  }
}
