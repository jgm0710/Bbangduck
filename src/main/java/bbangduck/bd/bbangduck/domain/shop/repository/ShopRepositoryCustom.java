package bbangduck.bd.bbangduck.domain.shop.repository;

import bbangduck.bd.bbangduck.domain.shop.entity.Shop;

import java.util.List;

public interface ShopRepositoryCustom {
    List<Shop> search(Shop shop);

    Shop deleteYN(Long shopId);

//    Page<Shop> searchPage(Shop shop, Pageable pageable);
}
