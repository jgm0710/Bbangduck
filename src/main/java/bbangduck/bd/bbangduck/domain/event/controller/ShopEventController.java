package bbangduck.bd.bbangduck.domain.event.controller;

import bbangduck.bd.bbangduck.domain.event.dto.ShopEventDto;
import bbangduck.bd.bbangduck.domain.event.entity.ShopEvent;
import bbangduck.bd.bbangduck.domain.event.service.EventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/event/shop")
@Slf4j
public class ShopEventController {
    private final EventService eventService;



    @GetMapping("/ing")
    public ResponseEntity<List<ShopEvent>> shopEventRunning() {
        return ResponseEntity.ok().body(this.eventService.runningEvent());
    }

    @GetMapping("/")
    public ResponseEntity<List<ShopEvent>> shopEventSearch(@RequestBody @Valid ShopEventDto shopEventDto) {
        List<ShopEvent> shopEvents = this.eventService.search(shopEventDto);

        return ResponseEntity.ok().body(shopEvents);
    }

    @PostMapping("/")
    public ResponseEntity<ShopEvent> shopEventSave(@RequestBody @Valid ShopEventDto shopEventDto) {

        ShopEvent shopEvent = this.eventService.shopEventSave(shopEventDto);

        return ResponseEntity.ok(shopEvent);
    }

    @PutMapping("/{shop_event_id}")
    public ResponseEntity<ShopEvent> shopEventUpdate(@RequestBody @Valid ShopEventDto updateShopEventDto,
                                                     @PathVariable("shop_event_id") Long oldShopId) {
        ShopEvent shopEvent = this.eventService.update(oldShopId, updateShopEventDto);
        return ResponseEntity.ok().body(shopEvent);
    }



}
