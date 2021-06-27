package bbangduck.bd.bbangduck.domain.shop.service;

import bbangduck.bd.bbangduck.domain.shop.dto.ShopImageDto;
import bbangduck.bd.bbangduck.domain.shop.entity.Shop;
import bbangduck.bd.bbangduck.domain.shop.entity.ShopImage;
import bbangduck.bd.bbangduck.domain.shop.repository.ShopImageRepository;
import bbangduck.bd.bbangduck.domain.shop.repository.ShopRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class ShopImageServiceImpl implements ShopImageService{

    private final ShopImageRepository shopImageRepository;

    @Override
    public List<ShopImage> findByShopId(Long shopId) {
        return shopImageRepository.findByShopId(shopId);
    }
    @Override
    public ShopImage saveImage(ShopImageDto shopImageDto, Shop shop) {
        ShopImage shopImage = ShopImage.toEntity(shopImageDto);
        shopImage.setShop(shop);
        this.shopImageRepository.save(shopImage);

        return shopImage;
    }
}
