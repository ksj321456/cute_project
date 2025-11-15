package kr.ac.hansung.cse.board_and_chatting.repository.board_repository;

import kr.ac.hansung.cse.board_and_chatting.dto.jpa_dto.board_dto.BoardDto;
import kr.ac.hansung.cse.board_and_chatting.entity.Board;
import kr.ac.hansung.cse.board_and_chatting.exception.exceptions.AuthenticationException;
import kr.ac.hansung.cse.board_and_chatting.exception.exceptions.ServerInternalException;
import kr.ac.hansung.cse.board_and_chatting.exception.status.ErrorStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@Slf4j
public class BoardJpaRepository implements BoardRepository {

    private final JpaBoardRepository boardRepository;

    @Autowired
    public BoardJpaRepository(JpaBoardRepository boardRepository) {
        this.boardRepository = boardRepository;
    }


    @Override
    public Board save(Board board) {
        try{
            return boardRepository.save(board);
        } catch (DataIntegrityViolationException e) {
            throw new AuthenticationException(ErrorStatus.ALREADY_EXISTS_ARTICLE);
        }
    }

    @Override
    public Board findBoardById(Long boardId) {
        Optional<Board> boardOptional = boardRepository.findBoardById(boardId);
        if (boardOptional.isPresent()) {
            return boardOptional.get();
        }
        return null;
    }

    // DB에 저장되어 있는 모든 게시물 보여주기
    @Override
    public Page<BoardDto> findAllWithUserAndCommentCount(Pageable pageable) {
        return boardRepository.findAllWithUserAndCommentCount(pageable);
    }

    @Override
    public Page<BoardDto> findAllByTitleCustom(String title, Pageable pageable) {
        return boardRepository.findAllByTitleCustom(title, pageable);
    }

    @Override
    public Page<BoardDto> findAllByTitleAndContentCustom(String title, String content, Pageable pageable) {
        return boardRepository.findAllByTitleAndContentCustom(title, content, pageable);
    }

    @Override
    public Board findBoardByIdCustom(Long boardId) {
        Optional<Board> boardOptional = boardRepository.findBoardByIdCustom(boardId);
        log.info("Repository Layer: findBoardByIdCustom is exist => " + boardOptional.isPresent());
        return boardOptional.orElseThrow(() -> new ServerInternalException(ErrorStatus.INTERNAL_BAD_REQUEST));
    }
}
