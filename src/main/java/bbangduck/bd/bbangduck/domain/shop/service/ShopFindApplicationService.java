package bbangduck.bd.bbangduck.domain.shop.service;

import bbangduck.bd.bbangduck.domain.shop.dto.controller.ShopLocationPageResponseDto;
import bbangduck.bd.bbangduck.domain.shop.dto.controller.ShopLocationResponseDto;
import bbangduck.bd.bbangduck.domain.shop.entity.Shop;
import bbangduck.bd.bbangduck.domain.shop.entity.embeded.Location;
import bbangduck.bd.bbangduck.domain.shop.mapper.ShopToLocationResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.Min;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ShopFindApplicationService {

  private final ShopFindService shopFindService;
  private final ShopService shopService;


  public ShopLocationPageResponseDto findAllByDistance(Location location, @Min(0) Integer distance) {
    List<Shop> list = shopFindService.findAllByKmDistance(location, distance);
    // TODO 페이징으로 변경
    List<ShopLocationResponseDto> result = list.stream().map(it -> ShopToLocationResponseDto.convert(it, location)).collect(Collectors.toList());
    return new ShopLocationPageResponseDto(result, 1, false, 10000);
  }

  public Shop getShop(Long shopId) {
    return shopService.getById(shopId);
  }
}
