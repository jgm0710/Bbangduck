package bbangduck.bd.bbangduck.domain.shop.entity;

import bbangduck.bd.bbangduck.domain.shop.dto.ShopDto;
import bbangduck.bd.bbangduck.domain.shop.entity.embeded.Location;
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

    public static Shop toEntity(ShopDto shopDto) {
        return Shop.builder()
                .id(shopDto.getId())
                .name(shopDto.getName())
                .shopUrl(shopDto.getShopUrl())
                .shopInfo(shopDto.getShopInfo())
                .address(shopDto.getAddress())
                .deleteYN(false)
                .build();

    }
    public static Shop toEntity(ShopDto shopDto, Area area, Franchise franchise) {
        return Shop.builder()
                .id(shopDto.getId())
                .franchise(franchise)
                .name(shopDto.getName())
                .shopInfo(shopDto.getShopInfo())
                .location(new Location(shopDto.getLat(), shopDto.getLon()))
                .address(shopDto.getAddress())
                .area(area)
                .deleteYN(false)
                .build();
    }

    public void addShopPrices(ShopPrice shopPrice) {
        this.shopPrices.add(shopPrice);
        shopPrice.setShop(this);
    }

    public double getLatitude() {
        return location.getLatitude();
    }

    public double getLongitude() {
        return location.getLongitude();
    }
}
