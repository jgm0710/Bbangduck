package bbangduck.bd.bbangduck.domain.shop.service;


import bbangduck.bd.bbangduck.domain.shop.entity.Shop;
import bbangduck.bd.bbangduck.domain.shop.entity.embeded.Location;
import bbangduck.bd.bbangduck.domain.shop.repository.AreaRepository;
import bbangduck.bd.bbangduck.domain.shop.repository.FranchiseRepository;
import bbangduck.bd.bbangduck.domain.shop.repository.ShopQueryRepository;
import bbangduck.bd.bbangduck.domain.shop.repository.ShopRepository;
import bbangduck.bd.bbangduck.global.common.util.DistanceUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@DisplayName("Shop 서비스 테스트")
class ShopServiceTest {

  private final ShopQueryRepository shopQueryRepository = Mockito.mock(ShopQueryRepository.class);
  private final ShopFindService shopService = new ShopFindService(shopQueryRepository);

  @Test
  @DisplayName("KM 단위의 거리 조회 테스트")
  void findAllByKmDistanceTest() {
    int DISTANCE = 5;
    double latitude = 126.73;
    double longitude = 37.41;
    double topLatitude = DistanceUtil.calculateLatitudeDistance(latitude, DISTANCE);
    double bottomLatitude = DistanceUtil.calculateLatitudeDistance(latitude, -DISTANCE);
    double leftLongitude = DistanceUtil.calculateLongitudeDistance(longitude, DISTANCE);
    double rightLongitude = DistanceUtil.calculateLongitudeDistance(longitude, -DISTANCE);
    List<Shop> tempList = new ArrayList<>();

    for (int i = 0; i < 10_000; i++) {
      double v = new Random().nextDouble();
      tempList.add(Shop.builder().location(Location.builder().longitude(longitude + v).latitude(latitude + v).build()).build());
    }

    when(shopQueryRepository.findByRangeLocation(topLatitude,bottomLatitude,leftLongitude,rightLongitude)).thenReturn(tempList);

    List<Shop> shops = shopService.findAllByKmDistance(new Location(latitude, longitude), DISTANCE);
    for (Shop shop : shops) {
      double distance = DistanceUtil.getDistance(Location.builder().longitude(latitude).longitude(longitude).build(), shop.getLocation());
      assertTrue(distance < DISTANCE);
    }
  }
}