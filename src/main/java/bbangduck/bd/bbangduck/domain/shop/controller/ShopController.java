package bbangduck.bd.bbangduck.domain.shop.controller;

import bbangduck.bd.bbangduck.domain.shop.dto.*;
import bbangduck.bd.bbangduck.domain.shop.entity.Shop;
import bbangduck.bd.bbangduck.domain.shop.entity.ShopImage;
import bbangduck.bd.bbangduck.domain.shop.entity.ShopPrice;
import bbangduck.bd.bbangduck.domain.shop.service.ShopService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.Response;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/shop")
@RequiredArgsConstructor
public class ShopController {
    private final ShopService shopService;

    @GetMapping("/{shop_id}")
    public ResponseEntity<Shop> shopSearch(@PathVariable("shop_id") Long shopId) {
        return ResponseEntity.ok().body(this.shopService.findById(shopId));
    }

    @GetMapping("/search")
    public ResponseEntity<List<Shop>> shopSearchList(@RequestBody @Valid ShopDto shopDto) {

        List<Shop> shops = this.shopService.search(shopDto);
        return ResponseEntity.ok().body(shops);
    }

    @PostMapping(value = "/")
    public ResponseEntity<Shop> shopSave(@RequestBody ShopDto shopDto,
                                         @RequestBody ShopImageDto shopImageDto) {
        Shop shop = this.shopService.save(shopDto, shopImageDto);
        return ResponseEntity.ok().body(shop);
    }


    @DeleteMapping("/{shop_id}")
    public ResponseEntity<Shop> shopDelete(@PathVariable("shop_id") Long shopId) {
        Shop shop = this.shopService.delete(shopId);
        return ResponseEntity.ok().body(shop);
    }

}
