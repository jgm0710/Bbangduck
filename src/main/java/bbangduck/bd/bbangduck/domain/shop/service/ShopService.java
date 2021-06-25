package bbangduck.bd.bbangduck.domain.shop.service;

import bbangduck.bd.bbangduck.domain.model.embeded.Location;
import bbangduck.bd.bbangduck.domain.shop.entity.Shop;
import bbangduck.bd.bbangduck.domain.shop.repository.ShopQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static bbangduck.bd.bbangduck.global.common.util.DistanceUtil.*;


@RequiredArgsConstructor
@Service
public class ShopService {

  private final ShopQueryRepository shopQueryRepository;
  
  public List<Shop> findAllByKmDistance(Location location, int distance) {
    List<Shop> byRangeLocation = shopQueryRepository.findByRangeLocation(
        calculateLatitudeDistance(location.getLatitude(), distance),
        calculateLatitudeDistance(location.getLatitude(), -distance),
        calculateLongitudeDistance(location.getLongitude(), distance),
        calculateLongitudeDistance(location.getLongitude(), -distance));
    return byRangeLocation
        .stream().filter(
            it -> getDistance(location, it.getLocation()) < distance
        ).collect(Collectors.toList());
  }
}
