package bbangduck.bd.bbangduck.domain.board.repository;

import bbangduck.bd.bbangduck.domain.board.entity.Board;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardRepository extends JpaRepository<Board, Long> {
    Board findByWriter(String writer);
}
