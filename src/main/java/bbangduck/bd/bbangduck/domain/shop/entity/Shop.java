package bbangduck.bd.bbangduck.domain.shop.entity;

import bbangduck.bd.bbangduck.domain.model.embeded.Location;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Shop {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "shop_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "franchise_id")
    private Franchise franchise;

    @OneToOne(mappedBy = "shop", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private ShopImage shopImage;

    @Column(name = "shop_name")
    private String name;

    @Column(length = 1000)
    private String shopUrl;


    @Column(length = 3000)
    private String shopInfo;

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

    @Builder
    public Shop(Long id, Franchise franchise, ShopImage shopImage, String name, String shopUrl, String shopInfo, Location location, String address, Area area, boolean deleteYN) {
        this.id = id;
        this.franchise = franchise;
        this.shopImage = shopImage;
        this.name = name;
        this.shopUrl = shopUrl;
        this.shopInfo = shopInfo;
        this.location = location;
        this.address = address;
        this.area = area;
        this.deleteYN = deleteYN;
    }

    public void addShopPrices(ShopPrice shopPrice) {
        this.shopPrices.add(shopPrice);
        shopPrice.setShop(this);
    }
}
