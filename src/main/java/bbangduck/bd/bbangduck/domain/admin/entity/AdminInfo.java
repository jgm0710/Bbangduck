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

    public static AdminInfo toEntity(AdminInfoDto adminInfoDto, Member member) {
        return AdminInfo.builder()
                .id(adminInfoDto.getId())
                .owner(adminInfoDto.getOwner())
                .telephone(adminInfoDto.getTelephone())
                .companyNum(adminInfoDto.getCompanyNum())
                .companyName(adminInfoDto.getCompanyName())
                .member(member)
                .deleteYN(false)
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
