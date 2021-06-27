package bbangduck.bd.bbangduck.domain.shop.service;

import bbangduck.bd.bbangduck.domain.shop.dto.AreaDto;
import bbangduck.bd.bbangduck.domain.shop.dto.FranchiseDto;
import bbangduck.bd.bbangduck.domain.shop.dto.ShopDto;
import bbangduck.bd.bbangduck.domain.shop.dto.ShopImageDto;
import bbangduck.bd.bbangduck.domain.shop.entity.Shop;
import bbangduck.bd.bbangduck.domain.shop.entity.ShopImage;
import bbangduck.bd.bbangduck.domain.shop.entity.embeded.Location;

import java.util.List;

public interface ShopService {


    List<Shop> search(ShopDto shopDto);

    List<Shop> findByAll();

    Shop findById(Long id);

    Shop save(ShopDto shopDto, ShopImageDto shopImageDto);

    void save(Shop shop);

    Shop delete(Long shopId);
}
