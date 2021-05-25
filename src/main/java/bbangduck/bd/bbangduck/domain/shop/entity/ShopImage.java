package bbangduck.bd.bbangduck.domain.shop.entity;

import bbangduck.bd.bbangduck.global.common.BaseEntityDateTime;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import javax.persistence.*;

// TODO: 2021-05-25 clear
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ShopImage extends BaseEntityDateTime {

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
