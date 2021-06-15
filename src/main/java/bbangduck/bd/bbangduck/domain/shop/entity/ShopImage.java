package bbangduck.bd.bbangduck.domain.shop.entity;

import bbangduck.bd.bbangduck.global.common.BaseEntityDateTime;
import lombok.*;

import javax.persistence.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Getter
@Table
public class ShopImage extends BaseEntityDateTime {

    public static ShopImage instance = new ShopImage();

    public static ShopImage getInstance() {
        return instance;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "shop_image_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_id")
    private Shop shop;

    private Long fileStorageId;

    private String fileName;

}
