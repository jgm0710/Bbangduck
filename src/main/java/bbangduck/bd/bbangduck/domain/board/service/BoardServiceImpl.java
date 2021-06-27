package bbangduck.bd.bbangduck.domain.board.service;

import bbangduck.bd.bbangduck.domain.board.entity.Board;
import bbangduck.bd.bbangduck.domain.board.repository.BoardRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class BoardServiceImpl implements BoardService{
    private final BoardRepository boardRepository;

    @Override
    public Board findById(Long id) {
        return this.boardRepository.findById(id).orElseThrow();
    }
}
