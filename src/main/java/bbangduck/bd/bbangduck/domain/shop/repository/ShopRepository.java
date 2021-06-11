package bbangduck.bd.bbangduck.domain.shop.repository;

import bbangduck.bd.bbangduck.domain.shop.entity.Shop;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShopRepository extends JpaRepository<Shop, Long>, ShopRepositoryCustom {
    Shop findByName(String shopName);
}
