package bbangduck.bd.bbangduck.domain.event.service;

import bbangduck.bd.bbangduck.domain.board.entity.Board;
import bbangduck.bd.bbangduck.domain.event.dto.ShopEventDto;
import bbangduck.bd.bbangduck.domain.event.entity.ShopEvent;
import bbangduck.bd.bbangduck.domain.event.repository.ShopEventRepository;
import bbangduck.bd.bbangduck.domain.shop.entity.Shop;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final ShopEventRepository shopEventRepository;
    @Override
    public void save(ShopEventDto shopEventDto, Board board, Shop shop) {
        this.shopEventRepository.save(ShopEvent.toEntity(shopEventDto, board, shop));
    }

}
