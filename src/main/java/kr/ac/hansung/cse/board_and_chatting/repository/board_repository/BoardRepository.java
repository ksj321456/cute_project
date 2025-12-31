package kr.ac.hansung.cse.board_and_chatting.repository.board_repository;

import kr.ac.hansung.cse.board_and_chatting.dto.jpa_dto.board_dto.BoardDto;
import kr.ac.hansung.cse.board_and_chatting.entity.Board;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BoardRepository {
    Board save(Board board);

    Board findBoardById(Long boardId);

    Page<BoardDto> findAllWithUserAndCommentCount(Pageable pageable);

    Page<BoardDto> findAllByTitleCustom(String title, Pageable pageable);

    Page<BoardDto> findAllByTitleAndContentCustom(String title, String content, Pageable pageable);

    Board findBoardByIdCustom(Long boardId);

    Page<Board> findAllByUserIdCustom(Long userId, Pageable pageable);
}
