package bbangduck.bd.bbangduck.domain.event.service;

import bbangduck.bd.bbangduck.domain.board.entity.Board;
import bbangduck.bd.bbangduck.domain.event.dto.ShopEventDto;
import bbangduck.bd.bbangduck.domain.shop.entity.Shop;

public interface EventService {
    void save(ShopEventDto shopEventDto, Board board, Shop shop);
}
