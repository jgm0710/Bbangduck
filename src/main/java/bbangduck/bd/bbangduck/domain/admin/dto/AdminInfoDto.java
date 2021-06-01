package bbangduck.bd.bbangduck.domain.admin.dto;

import bbangduck.bd.bbangduck.domain.admin.entity.AdminInfo;
import bbangduck.bd.bbangduck.domain.member.entity.Member;
import lombok.*;

import javax.persistence.*;

/**
 * Project : bbangduck
 * Create by IntelliJ IDEA
 * User: otrodevym
 * Date: 2021/5/30/0030
 * Time: 오후 10:53:40
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@ToString
@EqualsAndHashCode
public class AdminInfoDto {

    private Long id;

    private Long memberId;

    private String companyName;

    private String owner;

    private String address;

    private String companyNum;

    private String telephone;


    public static AdminInfoDto of(AdminInfo adminInfo) {
        return AdminInfoDto.builder()
                .id(adminInfo.getId())
                .owner(adminInfo.getOwner())
                .companyName(adminInfo.getCompanyName())
                .memberId(adminInfo.getMember().getId())
                .address(adminInfo.getAddress())
                .companyNum(adminInfo.getCompanyNum())
                .telephone(adminInfo.getTelephone())
                .build();
    }


}
