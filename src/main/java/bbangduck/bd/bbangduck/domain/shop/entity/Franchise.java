package bbangduck.bd.bbangduck.domain.shop.entity;

import bbangduck.bd.bbangduck.domain.admin.entity.AdminInfo;
import bbangduck.bd.bbangduck.global.common.BaseEntityDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Franchise extends BaseEntityDateTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "franchise_id")
    private Long id;

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

    @Builder
    public Franchise(Long id, AdminInfo adminInfo, String name, String owner, String ownerTelephone, boolean deleteYN) {
        this.id = id;
        this.adminInfo = adminInfo;
        this.name = name;
        this.owner = owner;
        this.ownerTelephone = ownerTelephone;
        this.deleteYN = deleteYN;
    }
}
