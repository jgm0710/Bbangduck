package bbangduck.bd.bbangduck.domain.shop.entity;

import bbangduck.bd.bbangduck.domain.admin.entity.AdminInfo;
import bbangduck.bd.bbangduck.global.common.BaseEntityDateTime;
import lombok.*;

import javax.persistence.*;

// TODO: 2021-05-25 완료
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Getter
@Table
public class Franchise extends BaseEntityDateTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "franchise_id")
    private Long id;

    // TODO: 2021-05-24 admin 추가
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id")
    private AdminInfo adminInfo;

    @Column(name = "franchise_name")
    private String name;

    @Column(name = "franchise_owner")
    private String owner;

    private String ownerTelephone;

    @Column(name = "delete_yn")
    private boolean deleteYN;

}
