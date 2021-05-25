package bbangduck.bd.bbangduck.domain.shop.entity;

import bbangduck.bd.bbangduck.domain.shop.entity.enumerate.ShopRatingType;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

// TODO: 2021-05-25 clear
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ShopRating {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "shop_rating_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_id")
    private Shop shop;

    @Column(name = "shop_rating_type")
    @Enumerated(EnumType.STRING)
    private ShopRatingType ratingType;

    @Column(name = "shop_rating")
    private float rating;

    @Column(name = "open_yn")
    private boolean openYN;

    @CreationTimestamp
    private LocalDateTime registerDate;
}
