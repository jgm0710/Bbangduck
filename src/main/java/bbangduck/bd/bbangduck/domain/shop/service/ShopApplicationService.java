package bbangduck.bd.bbangduck.domain.shop.service;

import bbangduck.bd.bbangduck.domain.shop.dto.controller.ShopLocationPageResponseDto;
import bbangduck.bd.bbangduck.domain.shop.dto.controller.ShopLocationResponseDto;
import bbangduck.bd.bbangduck.domain.shop.entity.Shop;
import bbangduck.bd.bbangduck.domain.shop.entity.embeded.Location;
import bbangduck.bd.bbangduck.domain.shop.mapper.ShopToLocationResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import javax.validation.constraints.Min;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class ShopApplicationService {

  private final ShopService shopService;

  public ShopLocationPageResponseDto findAllByDistance(Location location, @Min(0) Integer distance) {
    List<Shop> list = shopService.findAllByKmDistance(location, distance);
    // TODO 페이징으로 변경
    List<ShopLocationResponseDto> result = list.stream().map(it -> ShopToLocationResponseDto.convert(it, location)).collect(Collectors.toList());
    return new ShopLocationPageResponseDto(result, 1, false, 10000);
  }
}
