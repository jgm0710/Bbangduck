package bbangduck.bd.bbangduck.domain.shop.dto;

import lombok.*;

@ToString
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode
public class AreaDto {

    private Long id;

    private String code;

    private String name;

}
