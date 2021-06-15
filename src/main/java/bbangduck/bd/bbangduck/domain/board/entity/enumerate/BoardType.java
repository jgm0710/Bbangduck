package bbangduck.bd.bbangduck.domain.board.entity.enumerate;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum BoardType {
//    E : 이벤트
//    N : 공지사항
//    B : 배너
//    P : 푸시
    E("이벤트"), N("공지사항"), B("배너"), P("푸시");

    private final String description;

    public String getDescription() {
        return this.description;
    }
}
