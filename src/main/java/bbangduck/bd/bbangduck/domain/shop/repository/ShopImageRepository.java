package bbangduck.bd.bbangduck.domain.shop.repository;

import bbangduck.bd.bbangduck.domain.shop.entity.ShopImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShopImageRepository extends JpaRepository<ShopImage, Long> {
    List<ShopImage> findByShopId(Long shopId);
}
