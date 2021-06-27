package bbangduck.bd.bbangduck.domain.event.service;

import bbangduck.bd.bbangduck.domain.board.dto.BoardDto;
import bbangduck.bd.bbangduck.domain.board.entity.Board;
import bbangduck.bd.bbangduck.domain.board.repository.BoardRepository;
import bbangduck.bd.bbangduck.domain.event.dto.ShopEventDto;
import bbangduck.bd.bbangduck.domain.event.entity.ShopEvent;
import bbangduck.bd.bbangduck.domain.event.repository.ShopEventRepository;
import bbangduck.bd.bbangduck.domain.shop.dto.ShopDto;
import bbangduck.bd.bbangduck.domain.shop.entity.Shop;
import bbangduck.bd.bbangduck.domain.shop.repository.ShopRepository;
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
    private final BoardRepository boardResposototy;
    private final ShopRepository shopRepository;

    @Override
    public ShopEvent update(Long id, ShopEventDto newShopEventDto) {
        Optional<ShopEvent> oldShopEvent = this.shopEventRepository.findById(id);
        oldShopEvent.ifPresent(shopEventTemp -> {
            shopEventTemp.setStartTimes(newShopEventDto.getStartTimes());
            shopEventTemp.setEndTimes(newShopEventDto.getEndTimes());
            this.shopEventRepository.save(shopEventTemp);
        });


        return oldShopEvent.orElseThrow();

    }

    @Override
    public List<ShopEvent> runningEvent() {
        return this.shopEventRepository.findByStartTimesLessThanEqualAndEndTimesGreaterThanEqual(LocalDateTime.now(),
                LocalDateTime.now());
    }

    @Override
    public List<ShopEvent> search(ShopEventDto shopEventDto) {
        Board board = this.boardResposototy.findById(shopEventDto.getBoardId()).orElseThrow();
        Shop shop = this.shopRepository.findById(shopEventDto.getShopId()).orElseThrow();

        ShopEvent shopEvent = ShopEvent.toEntity(shopEventDto, board, shop);

        return this.shopEventRepository.search(shopEvent);
    }

    @Override
    public ShopEvent shopEventSave(ShopEventDto shopEventDto) {
        Board board = this.boardResposototy.findById(shopEventDto.getBoardId()).orElseThrow();
        Shop shop = this.shopRepository.findById(shopEventDto.getShopId()).orElseThrow();
        ShopEvent shopEvent = ShopEvent.toEntity(shopEventDto, board, shop);

        this.shopEventRepository.save(shopEvent);
        return shopEvent;
    }


}
