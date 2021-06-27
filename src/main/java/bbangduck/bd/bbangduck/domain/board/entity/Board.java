package bbangduck.bd.bbangduck.domain.board.entity;

import bbangduck.bd.bbangduck.domain.board.entity.enumerate.BoardType;
import bbangduck.bd.bbangduck.domain.admin.entity.AdminInfo;
import bbangduck.bd.bbangduck.domain.board.dto.BoardDto;
import bbangduck.bd.bbangduck.global.common.BaseEntityDateTime;
import lombok.*;

import javax.persistence.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Getter
@Table
public class Board extends BaseEntityDateTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "board_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id")
    private AdminInfo adminInfo;

    @Column(name = "board_type")
    @Enumerated(EnumType.STRING)
    private BoardType type;

    @Column(name = "board_title")
    private String title;

    @Column(name = "board_content")
    private String content;

    private String writer;

    public static Board toEntity(BoardDto boardDto, AdminInfo adminInfo) {
                return Board.builder()
                .writer(boardDto.getWriter())
                .type(boardDto.getType())
                .id(boardDto.getId())
                .title(boardDto.getTitle())
                .content(boardDto.getContent())
                .adminInfo(adminInfo)
                .build();
    }
}
