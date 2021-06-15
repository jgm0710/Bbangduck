package bbangduck.bd.bbangduck.domain.event.service;

import bbangduck.bd.bbangduck.domain.board.entity.Board;
import bbangduck.bd.bbangduck.domain.event.dto.ShopEventDto;
import bbangduck.bd.bbangduck.domain.event.entity.ShopEvent;
import bbangduck.bd.bbangduck.domain.shop.entity.Shop;

import java.util.List;
import java.util.Optional;

public interface EventService {
    void save(ShopEventDto shopEventDto, Board board, Shop shop);

    Optional<ShopEvent> update(Long id, ShopEvent newShopEvent);

    List<ShopEvent> runningEvent();
}
