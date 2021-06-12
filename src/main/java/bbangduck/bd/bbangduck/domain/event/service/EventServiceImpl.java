package bbangduck.bd.bbangduck.domain.event.service;

import bbangduck.bd.bbangduck.domain.board.entity.Board;
import bbangduck.bd.bbangduck.domain.event.dto.ShopEventDto;
import bbangduck.bd.bbangduck.domain.event.entity.ShopEvent;
import bbangduck.bd.bbangduck.domain.event.repository.ShopEventRepository;
import bbangduck.bd.bbangduck.domain.shop.entity.Shop;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class EventServiceImpl implements EventService {

    private final ShopEventRepository shopEventRepository;
    @Override
    public void save(ShopEventDto shopEventDto, Board board, Shop shop) {
        this.shopEventRepository.save(ShopEvent.toEntity(shopEventDto, board, shop));
    }

    @Override
    public Optional<ShopEvent> update(Long id, ShopEvent newShopEvent) {
        Optional<ShopEvent> oldShopEvent = this.shopEventRepository.findById(id);
        oldShopEvent.ifPresent(shopEventTemp -> {
            shopEventTemp.setStartTimes(newShopEvent.getStartTimes());
            shopEventTemp.setEndTimes(newShopEvent.getEndTimes());
            this.shopEventRepository.save(shopEventTemp);
        });


        return oldShopEvent;

    }

    @Override
    public List<ShopEvent> runningEvent() {
        return this.shopEventRepository.findByStartTimesLessThanEqualAndEndTimesGreaterThanEqual(LocalDateTime.now(),
                LocalDateTime.now());
    }

}
