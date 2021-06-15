package bbangduck.bd.bbangduck.domain.event.dto;

import lombok.*;

import java.time.LocalDateTime;

@ToString
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode
public class ShopEventDto {
    private Long id;
    private Long shopId;
    private Long boardId;

    private LocalDateTime startTimes;
    private LocalDateTime endTimes;

}
