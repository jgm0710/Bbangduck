package bbangduck.bd.bbangduck.domain.shop.entity;

import bbangduck.bd.bbangduck.domain.shop.entity.enumerate.ShopPriceUnit;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table
public class ShopPrice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "shop_price_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_id")
    private Shop shop;

    @Column(name = "shop_price")
    private int price;

    @Column(name = "shop_price_unit")
    @Enumerated(EnumType.STRING)
    private ShopPriceUnit priceUnit;

    @CreationTimestamp
    private LocalDateTime registerTimes;
}
