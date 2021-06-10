package bbangduck.bd.bbangduck.domain.event.repository;

import bbangduck.bd.bbangduck.domain.shop.entity.Shop;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShopRepository extends JpaRepository<Shop, Long> {
    Shop findByName(String shopName);
}
