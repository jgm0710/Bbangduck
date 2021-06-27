package bbangduck.bd.bbangduck.domain.shop.dto;

import lombok.*;

@ToString
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode
public class FranchiseDto {

    private Long id;

    private String name;

    private String owner;

    private String ownerTelephone;

    private boolean deleteYN;

}
