package bbangduck.bd.bbangduck.domain.shop.service;

import bbangduck.bd.bbangduck.domain.shop.dto.AreaDto;
import bbangduck.bd.bbangduck.domain.shop.dto.FranchiseDto;
import bbangduck.bd.bbangduck.domain.shop.dto.ShopDto;
import bbangduck.bd.bbangduck.domain.shop.dto.ShopImageDto;
import bbangduck.bd.bbangduck.domain.shop.entity.Area;
import bbangduck.bd.bbangduck.domain.shop.entity.Franchise;
import bbangduck.bd.bbangduck.domain.shop.entity.Shop;
import bbangduck.bd.bbangduck.domain.shop.entity.ShopImage;
import bbangduck.bd.bbangduck.domain.shop.entity.embeded.Location;
import bbangduck.bd.bbangduck.domain.shop.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static bbangduck.bd.bbangduck.global.common.util.DistanceUtil.*;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class ShopServiceImpl implements ShopService{

    private final ShopRepository shopRepository;
    private final AreaRepository areaRepository;
    private final FranchiseRepository franchiseRepository;
    private final ShopImageService shopImageService;


    private final ShopQueryRepository shopQueryRepository;


    @Override
    public List<Shop> search(ShopDto shopDto) {


        Shop shop = Shop.toEntity(shopDto);
        return this.shopRepository.search(shop);
    }


    @Override
    public List<Shop> findByAll() {
        return this.shopRepository.findAll();
    }

    @Override
    public Shop findById(Long id) {
        return this.shopRepository.findById(id).orElseThrow();
    }

    @Override
    public Shop save(ShopDto shopDto, ShopImageDto shopImageDto) {

        Area area = this.areaRepository.findById(shopDto.getAreaId()).orElseThrow();
        Franchise franchise = this.franchiseRepository.findById(shopDto.getFranchiseId()).orElseThrow();

        Shop shop = Shop.toEntity(shopDto, area, franchise);
        shopImageService.saveImage(shopImageDto, shop);

        this.shopRepository.save(shop);

        return shop;
    }

    @Override
    public void save(Shop shop) {
        this.shopRepository.save(shop);
    }

    @Override
    public Shop delete(Long shopId) {
        return this.shopRepository.deleteYN(shopId);
    }

}
