package bbangduck.bd.bbangduck.domain.shop.service;

import bbangduck.bd.bbangduck.domain.shop.dto.service.ShopCreateCommand;
import bbangduck.bd.bbangduck.domain.shop.entity.Shop;
import bbangduck.bd.bbangduck.domain.shop.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class ShopService {

    private final ShopRepository shopRepository;

    public Shop save(ShopCreateCommand shopCreateCommand){
        Shop shop = Shop.of(shopCreateCommand);
        return shopRepository.save(shop);
    }

    public void delete(Long shopId) {
        shopRepository.findById(shopId).orElseThrow().delete();
    }

    public Shop getById(Long shopId) {
        Shop shop = shopRepository.findById(shopId).orElseThrow();
        if(shop.isDeleted()){
            throw new RuntimeException();
        }
        return shop;
    }
}
