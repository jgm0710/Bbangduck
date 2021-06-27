package bbangduck.bd.bbangduck.domain.shop.dto;

import lombok.*;

@ToString
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode
public class ShopImageDto {

    private Long id;

    private Long fileStorageId;

    private String fileName;
}
