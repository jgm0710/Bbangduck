package bbangduck.bd.bbangduck.domain.shop.controller;

import bbangduck.bd.bbangduck.domain.shop.dto.controller.ShopCreateDto;
import bbangduck.bd.bbangduck.domain.shop.dto.controller.ShopFindByLocationRequestDto;
import bbangduck.bd.bbangduck.domain.shop.dto.controller.ShopLocationPageResponseDto;
import bbangduck.bd.bbangduck.domain.shop.dto.controller.ShopSearchDto;
import bbangduck.bd.bbangduck.domain.shop.entity.Shop;
import bbangduck.bd.bbangduck.domain.shop.entity.embeded.Location;
import bbangduck.bd.bbangduck.domain.shop.service.ShopFindApplicationService;
import bbangduck.bd.bbangduck.domain.shop.service.ShopApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/shops")
public class ShopRestController {

  private final ShopFindApplicationService shopFindApplicationService;
  private final ShopApplicationService shopApplicationService;

  private final static Integer DISTANCE = 5;

  @GetMapping("/now-location")
  public ResponseEntity<ShopLocationPageResponseDto> getShopsByNowLocation(
      ShopFindByLocationRequestDto request
  ) {
    return ResponseEntity.ok(
        shopFindApplicationService.findAllByDistance(new Location(request.getLatitude(), request.getLongitude()), DISTANCE)
    );
  }

  @PostMapping(value = "/")
  public ResponseEntity<Shop> shopSave(ShopCreateDto shopCreateDto) {
    Shop shop = shopApplicationService.saveShop(shopCreateDto.toCreateDto());
    return ResponseEntity.ok().body(shop);
  }

  @DeleteMapping("/{shop_id}")
  public ResponseEntity<?> shopDelete(@PathVariable("shop_id") Long shopId) {
    this.shopApplicationService.delete(shopId);
    return ResponseEntity.noContent().build();
  }


  @GetMapping("/{shop_id}")
  public ResponseEntity<Shop> shopSearch(@PathVariable("shop_id") Long shopId) {
    Shop shop = shopFindApplicationService.getShop(shopId);
    return ResponseEntity.ok(shop);
  }


  @GetMapping("/search")
  public ResponseEntity<List<Shop>> shopSearchList(ShopSearchDto shopSearchDto) {
    List<Shop> shops = shopFindApplicationService.search(shopSearchDto);
    return ResponseEntity.ok().body(shops);
  }
}
