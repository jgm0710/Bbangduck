package bbangduck.bd.bbangduck.domain.event.dto;

import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class ThemeEventDto {

    private Long id;
    private Long themeId;
    private Long shopEventId;

}
