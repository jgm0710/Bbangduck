package bbangduck.bd.bbangduck.domain.shop.controller;

import bbangduck.bd.bbangduck.domain.shop.entity.ShopImage;
import bbangduck.bd.bbangduck.domain.shop.service.ShopImageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/shop")
@RequiredArgsConstructor
public class ShopImageController {
    private final ShopImageService shopImageService;

    @GetMapping("/{shop_id}/images")
    public ResponseEntity<List<ShopImage>> shopImageSearch(@PathVariable("shop_id") Long shopId) {
        List<ShopImage> list = this.shopImageService.findByShopId(shopId);
        return ResponseEntity.ok().body(list);
    }
}
