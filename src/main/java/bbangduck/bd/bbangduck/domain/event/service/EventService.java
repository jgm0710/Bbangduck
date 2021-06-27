package bbangduck.bd.bbangduck.domain.event.service;

import bbangduck.bd.bbangduck.domain.board.dto.BoardDto;
import bbangduck.bd.bbangduck.domain.event.dto.ShopEventDto;
import bbangduck.bd.bbangduck.domain.event.entity.ShopEvent;
import bbangduck.bd.bbangduck.domain.shop.dto.ShopDto;

import java.util.List;

public interface EventService {

    ShopEvent update(Long id, ShopEventDto newShopEventDto);

    List<ShopEvent> runningEvent();

    List<ShopEvent> search(ShopEventDto shopEventDto);

    ShopEvent shopEventSave(ShopEventDto shopEventDto);
}
