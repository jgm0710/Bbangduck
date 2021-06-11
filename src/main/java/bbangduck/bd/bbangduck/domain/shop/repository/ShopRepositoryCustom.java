package bbangduck.bd.bbangduck.domain.shop.repository;

import bbangduck.bd.bbangduck.domain.admin.entity.AdminInfo;
import bbangduck.bd.bbangduck.domain.shop.entity.Shop;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ShopRepositoryCustom {
    List<Shop> search(Shop shop);

    Page<Shop> searchPage(Shop shop, Pageable pageable);
}
