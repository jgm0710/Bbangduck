package bbangduck.bd.bbangduck.domain.shop.controller;

import bbangduck.bd.bbangduck.domain.model.embeded.Location;
import bbangduck.bd.bbangduck.domain.shop.dto.controller.ShopFindByLocationRequestDto;
import bbangduck.bd.bbangduck.domain.shop.dto.controller.ShopLocationPageResponseDto;
import bbangduck.bd.bbangduck.domain.shop.service.ShopApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/shops")
public class ShopRestController {

  private final ShopApplicationService shopApplicationService;

  private final static Integer DISTANCE = 5;

  @GetMapping("/now-location")
  public ResponseEntity<ShopLocationPageResponseDto> getShopsByNowLocation(
      ShopFindByLocationRequestDto request
  ) {
    return ResponseEntity.ok(
        shopApplicationService.findAllByDistance(new Location(request.getLatitude(), request.getLongitude()), DISTANCE)
    );
  }
}
