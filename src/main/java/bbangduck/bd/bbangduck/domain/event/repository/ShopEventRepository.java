package bbangduck.bd.bbangduck.domain.event.repository;

import bbangduck.bd.bbangduck.domain.event.entity.ShopEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;


public interface ShopEventRepository extends JpaRepository<ShopEvent, Long>, ShopEventRepositoryCustom {

    List<ShopEvent> findByStartTimesLessThanEqualAndEndTimesGreaterThanEqual(LocalDateTime now, LocalDateTime now1);
}
