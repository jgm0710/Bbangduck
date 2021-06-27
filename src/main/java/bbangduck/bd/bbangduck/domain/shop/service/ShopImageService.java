package bbangduck.bd.bbangduck.domain.shop.service;

import bbangduck.bd.bbangduck.domain.shop.dto.ShopImageDto;
import bbangduck.bd.bbangduck.domain.shop.entity.Shop;
import bbangduck.bd.bbangduck.domain.shop.entity.ShopImage;

import java.util.List;

public interface ShopImageService {

    List<ShopImage> findByShopId(Long shopId);
    ShopImage saveImage(ShopImageDto shopImageDto, Shop shop);
}
