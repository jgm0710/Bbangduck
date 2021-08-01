package bbangduck.bd.bbangduck.domain.shop.entity;

import bbangduck.bd.bbangduck.domain.shop.dto.service.ShopCreateCommand;
import bbangduck.bd.bbangduck.domain.shop.entity.embeded.Location;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table
public class Shop {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "shop_id")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "franchise_id")
  private Franchise franchise;

  @Embedded
  private ShopImage shopImage;

  @Column(name = "shop_name")
  private String name;

  @Column(length = 1000)
  private String shopUrl;


  @Column(length = 3000)
  private String description;

  @OneToMany(mappedBy = "shop", cascade = CascadeType.ALL)
  private List<ShopPrice> shopPrices;

  @Embedded
  private Location location;

  private String address;

  @Column(name = "delete_yn")
  private boolean deleteYN;


  @Builder
  public Shop(Long id, Franchise franchise, ShopImage shopImage, String name, String shopUrl, String description, List<ShopPrice> shopPrices, Location location, String address, boolean deleteYN) {
    this.id = id;
    this.franchise = franchise;
    this.shopImage = shopImage;
    this.name = name;
    this.shopUrl = shopUrl;
    this.description = description;
    this.shopPrices = shopPrices;
    this.location = location;
    this.address = address;
    this.deleteYN = deleteYN;
  }


  public static Shop of(ShopCreateCommand command) {
    return Shop.builder()
        .franchise(command.getFranchise())
        .shopImage(ShopImage.of(command.getFileStorageId(), command.getFileName()))
        .description(command.getDescription())
        .name(command.getName())
        .location(Location.of(command.getLatitude(), command.getLongitude()))
        .address(command.getAddress())
        .shopUrl(command.getShopUrl())
        .build();
  }

  public void addShopPrices(ShopPrice shopPrice) {
    this.shopPrices.add(shopPrice);
    shopPrice.setShop(this);
  }

  public Long getId() {
    return id;
  }

  public double getLatitude() {
    return location.getLatitude();
  }

  public double getLongitude() {
    return location.getLongitude();
  }

  public Location getLocation() {
    return location;
  }

  public String getName() {
    return name;
  }

  public String getImageUrl() {
    return shopImage.getFileName();
  }

  public String getUrl() {
    return shopUrl;
  }

  public String getDescription() {
    return description;
  }

  public String getAddress() {
    return address;
  }

  public void delete() {
    deleteYN = true;
  }

  public boolean isDeleted() {
    return deleteYN;
  }

  public Franchise getFranchise() {
    return franchise;
  }

}
