package bbangduck.bd.bbangduck.domain.event.entity;

import bbangduck.bd.bbangduck.domain.board.entity.Board;
import bbangduck.bd.bbangduck.domain.event.dto.ShopEventDto;
import bbangduck.bd.bbangduck.domain.shop.entity.Shop;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Getter
@Table
public class ShopEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "shop_event_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_id")
    private Shop shop;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    private Board board;

    @Setter
    private LocalDateTime startTimes;

    @Setter
    private LocalDateTime endTimes;

    public static ShopEvent toEntity(ShopEventDto shopEventDto, Board board, Shop shop) {
        return ShopEvent.builder()
                .id(shopEventDto.getId())
                .board(board)
                .shop(shop)
                .endTimes(shopEventDto.getEndTimes())
                .startTimes(shopEventDto.getStartTimes())
                .build();
    }

    @Override
    public String toString() {
        return "ShopEvent{" +
                "id=" + id +
                ", startTimes=" + startTimes +
                ", endTimes=" + endTimes +
                '}';
    }
}
