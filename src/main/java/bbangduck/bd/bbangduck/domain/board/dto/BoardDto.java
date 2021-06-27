package bbangduck.bd.bbangduck.domain.board.dto;

import bbangduck.bd.bbangduck.domain.board.entity.enumerate.BoardType;
import lombok.*;

@ToString
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode
public class BoardDto {

    private Long id;
    private BoardType type;
    private Long adminId;
    private String title;
    private String content;
    private String writer;

}
