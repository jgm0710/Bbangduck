package bbangduck.bd.bbangduck.domain.shop.controller;

import bbangduck.bd.bbangduck.domain.shop.dto.controller.*;
import bbangduck.bd.bbangduck.domain.shop.entity.Shop;
import bbangduck.bd.bbangduck.domain.shop.entity.embeded.Location;
import bbangduck.bd.bbangduck.domain.shop.service.ShopApplicationService;
import bbangduck.bd.bbangduck.domain.shop.service.ShopFindApplicationService;
import bbangduck.bd.bbangduck.global.common.SliceResultResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/shops")
public class ShopRestController {

  private final ShopFindApplicationService shopFindApplicationService;
  private final ShopApplicationService shopApplicationService;

  private final static Integer DISTANCE = 5;

  @GetMapping("/now-location")
  @ResponseStatus(HttpStatus.OK)
  public SliceResultResponseDto<ShopLocationResponseDto> getShopsByNowLocation(
      ShopFindByLocationRequestDto request
  ) {
    Location location = new Location(request.getLatitude(), request.getLongitude());
    SliceResultResponseDto<Shop> result = shopFindApplicationService.findAllByDistance(location, DISTANCE, request.getAmount(),request.getPageNum());
    return result.convert(it -> ShopLocationResponseDto.byWith(it, location));
  }

  @PostMapping
  public ResponseEntity<ShopResponseDto> shopSave(ShopCreateDto shopCreateDto) {
    Shop shop = shopApplicationService.saveShop(shopCreateDto.toCreateDto());
    return ResponseEntity.ok().body(ShopResponseDto.by(shop));
  }

  @DeleteMapping("/{shop_id}")
  public ResponseEntity<?> shopDelete(@PathVariable("shop_id") Long shopId) {
    this.shopApplicationService.delete(shopId);
    return ResponseEntity.noContent().build();
  }


  @GetMapping("/{shop_id}")
  @ResponseStatus(HttpStatus.OK)
  public ShopResponseDto shopFindById(@PathVariable("shop_id") Long shopId) {
    Shop shop = shopFindApplicationService.getShop(shopId);
    return ShopResponseDto.by(shop);
  }
}
