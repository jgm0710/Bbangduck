package bbangduck.bd.bbangduck.domain.event.repository;

import bbangduck.bd.bbangduck.domain.event.entity.ShopEvent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShopEventRepository extends JpaRepository<ShopEvent, Long> {

}
