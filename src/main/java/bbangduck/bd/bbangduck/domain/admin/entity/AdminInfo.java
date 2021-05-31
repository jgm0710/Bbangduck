package bbangduck.bd.bbangduck.domain.admin.entity;

import bbangduck.bd.bbangduck.domain.admin.dto.AdminInfoDto;
import bbangduck.bd.bbangduck.domain.member.entity.Member;
import bbangduck.bd.bbangduck.global.common.BaseEntityDateTime;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.convert.Jsr310Converters;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Table
@Builder
public class AdminInfo extends BaseEntityDateTime{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "admin_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    private String companyName;

    private String owner;

    private String address;

    private String companyNum;

    private String telephone;

    @Column(name = "delete_yn")
    private boolean deleteYN;

//    @Builder
//    public AdminInfo(Member member, String companyName, String owner, String address, String companyNum, String telephone, boolean deleteYN) {
//        this.member = member;
//        this.companyName = companyName;
//        this.owner = owner;
//        this.address = address;
//        this.companyNum = companyNum;
//        this.telephone = telephone;
//        this.deleteYN = deleteYN;
//    }

    public AdminInfoDto of() {
        return AdminInfoDto.builder()
                .address(this.getAddress())
                .companyNum(this.getCompanyNum())
                .companyName(this.getCompanyName())
                .telephone(this.getTelephone())
                .id(this.getId())
                .member(this.getMember())
                .owner(this.getOwner())
                .build();
    }

    @Override
    public String toString() {
        return "AdminInfo{" +
                "id=" + id +
                ", companyName='" + companyName + '\'' +
                ", owner='" + owner + '\'' +
                ", address='" + address + '\'' +
                ", companyNum='" + companyNum + '\'' +
                ", telephone='" + telephone + '\'' +
                ", deleteYN=" + deleteYN +
                '}';
    }
}
