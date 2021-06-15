package bbangduck.bd.bbangduck.domain.shop.entity;

import bbangduck.bd.bbangduck.domain.model.embeded.Location;
import lombok.*;

import javax.persistence.*;
import java.util.List;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Getter
@Table
public class Shop {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "shop_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "franchise_id")
    private Franchise franchise;

    @Setter
    @OneToOne(mappedBy = "shop", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private ShopImage shopImage;

    @Column(name = "shop_name")
    private String name;

    @Column(length = 1000)
    private String shopUrl;


    @Column(length = 3000)
    private String shopInfo;

    @Setter
    @OneToMany(mappedBy = "shop", cascade = CascadeType.ALL)
    private List<ShopPrice> shopPrices;

    @Embedded
    private Location location;

    private String address;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "area_id")
    private Area area;

    @Column(name = "delete_yn")
    private boolean deleteYN;

    public void addShopPrices(ShopPrice shopPrice) {
        this.shopPrices.add(shopPrice);
        shopPrice.setShop(this);
    }
}
