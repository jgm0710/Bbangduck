package bbangduck.bd.bbangduck.domain.event.repository;

import bbangduck.bd.bbangduck.domain.event.entity.ShopEvent;
import bbangduck.bd.bbangduck.domain.shop.entity.Shop;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ShopEventRepositoryCustom {
    List<ShopEvent> search(ShopEvent shopEvent);

    Page<ShopEvent> searchPage(ShopEvent shopEvent, Pageable pageable);
}
