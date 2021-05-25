package bbangduck.bd.bbangduck.domain.board.entity;

import bbangduck.bd.bbangduck.domain.board.entity.enumerate.BoardType;
import bbangduck.bd.bbangduck.domain.member.entity.AdminInfo;
import bbangduck.bd.bbangduck.global.common.BaseEntityDateTime;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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

}
