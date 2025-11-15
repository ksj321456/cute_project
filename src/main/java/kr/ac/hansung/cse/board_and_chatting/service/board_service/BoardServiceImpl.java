package kr.ac.hansung.cse.board_and_chatting.service.board_service;

import kr.ac.hansung.cse.board_and_chatting.dto.jpa_dto.board_dto.BoardDto;
import kr.ac.hansung.cse.board_and_chatting.dto.jpa_dto.comment_dto.CommentDto;
import kr.ac.hansung.cse.board_and_chatting.dto.request_dto.BoardRequestDto;
import kr.ac.hansung.cse.board_and_chatting.dto.response_dto.BoardResponseDto;
import kr.ac.hansung.cse.board_and_chatting.entity.Board;
import kr.ac.hansung.cse.board_and_chatting.entity.User;
import kr.ac.hansung.cse.board_and_chatting.exception.exceptions.AuthenticationException;
import kr.ac.hansung.cse.board_and_chatting.exception.status.ErrorStatus;
import kr.ac.hansung.cse.board_and_chatting.repository.board_repository.BoardRepository;
import kr.ac.hansung.cse.board_and_chatting.repository.comment_repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class BoardServiceImpl implements BoardService {

    private final BoardRepository boardRepository;

    private final CommentRepository commentRepository;


    @Transactional
    public Board saveArticle(BoardRequestDto.CreateArticleRequest createArticleRequest, User user) {
        Board board = Board.builder()
                .title(createArticleRequest.getTitle())
                .category(createArticleRequest.getCategory())
                .content(createArticleRequest.getContent())
                .user(user)
                .build();

        // 중복된 Title의 게시글이 이미 있다면 => 예외 처리
        if (boardRepository.findBoardById(board.getId()) != null) {
            throw new AuthenticationException(ErrorStatus.ALREADY_EXISTS_ARTICLE);
        }

        return boardRepository.save(board);
    }

    public BoardResponseDto.GeneralArticlesResponseDto getArticle(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<BoardDto> boards = boardRepository.findAllWithUserAndCommentCount(pageable);

        long totalPages = boards.getTotalPages();
        List<BoardResponseDto.ArticleResponseDto> articles = new ArrayList<>();

        for (BoardDto board : boards.getContent()) {
            BoardResponseDto.ArticleResponseDto articleResponseDto = BoardResponseDto.ArticleResponseDto.builder()
                    .boardId(board.getBoardId())
                    .title(board.getTitle())
                    .author(board.getAuthor())
                    .category(board.getCategory())
                    .commentCount(board.getCommentCount())  // BoardDto에서 바로 사용
                    .like(board.getLike())
                    .dislike(board.getDislike())
                    .createdAt(board.getCreatedAt())
                    .updatedAt(board.getUpdatedAt())
                    .build();

            articles.add(articleResponseDto);
        }

        return BoardResponseDto.GeneralArticlesResponseDto.builder()
                .totalPages(totalPages)
                .articles(articles)
                .build();
    }


    @Override
    public BoardResponseDto.GeneralArticlesResponseDto getArticlesWithTitle(String title, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<BoardDto> boards = boardRepository.findAllByTitleCustom(title, pageable);
        long totalPages = boards.getTotalPages();

        List<Long> boardIds = new ArrayList<>();
        for (BoardDto board : boards.getContent()) {
            boardIds.add(board.getBoardId());
        }

        List<CommentDto> commentDtoList = commentRepository.findCommentCountCustom(boardIds);

        List<BoardResponseDto.ArticleResponseDto> articles = new ArrayList<>();

        for (int i = 0; i < boards.getContent().size(); i++) {
            BoardDto board = boards.getContent().get(i);
            BoardResponseDto.ArticleResponseDto articleResponseDto = BoardResponseDto.ArticleResponseDto.builder()
                    .boardId(board.getBoardId())
                    .title(board.getTitle())
                    .category(board.getCategory())
                    .author(board.getAuthor())
                    .like(board.getLike())
                    .dislike(board.getDislike())
                    .commentCount(commentDtoList.get(0).getCommentCount())
                    .createdAt(board.getCreatedAt())
                    .updatedAt(board.getUpdatedAt())
                    .build();
            articles.add(articleResponseDto);
        }

        BoardResponseDto.GeneralArticlesResponseDto generalArticlesResponseDto = BoardResponseDto.GeneralArticlesResponseDto
                .builder()
                .totalPages(totalPages)
                .articles(articles)
                .build();

        return generalArticlesResponseDto;
    }

    @Override
    public BoardResponseDto.GeneralArticlesResponseDto getArticleWithTitleOrContent(String title, String content, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<BoardDto> boards = boardRepository.findAllByTitleAndContentCustom(title, content, pageable);
        long totalPages = boards.getTotalPages();

        List<Long> boardIds = new ArrayList<>();
        for (BoardDto board : boards.getContent()) {
            boardIds.add(board.getBoardId());
        }

        List<CommentDto> commentDtoList = commentRepository.findCommentCountCustom(boardIds);

        List<BoardResponseDto.ArticleResponseDto> articles = new ArrayList<>();

        for (int i = 0; i < boards.getContent().size(); i++) {
            BoardDto board = boards.getContent().get(i);
            BoardResponseDto.ArticleResponseDto articleResponseDto = BoardResponseDto.ArticleResponseDto.builder()
                    .boardId(board.getBoardId())
                    .title(board.getTitle())
                    .category(board.getCategory())
                    .author(board.getAuthor())
                    .like(board.getLike())
                    .dislike(board.getDislike())
                    .commentCount(commentDtoList.get(0).getCommentCount())
                    .createdAt(board.getCreatedAt())
                    .updatedAt(board.getUpdatedAt())
                    .build();
            articles.add(articleResponseDto);
        }

        BoardResponseDto.GeneralArticlesResponseDto generalArticlesResponseDto = BoardResponseDto.GeneralArticlesResponseDto.builder()
                .totalPages(totalPages)
                .articles(articles)
                .build();
        return generalArticlesResponseDto;
    }

    @Override
    // id를 통해 Article 검색
    // authority를 통해 if 관리자 -> 수정, 삭제 권한 있음
    // else -> 해당 게시물을 작성한 회원일 경우에만 수정, 삭제 가능 그 외의 회원은 수정, 삭제 불가
    public BoardResponseDto.OneArticleResponseDto getOneArticle(Long id, User user, int commentPage, int commentSize) {
        // id를 통해 해당 글을 작성한 사람 GET
        log.info("Service Layer: getOneArticle parameters => id = " +  id + ", user_nickname = " + user.getNickname());
        Board board = boardRepository.findBoardByIdCustom(id);

        List<CommentDto> queryResult = commentRepository.findCommentByBoardIdCustom(board.getId(), PageRequest.of(commentPage, commentSize));

        // 해당 게시물을 작성한 사람과 요청한 사람이 같은 경우 => 수정, 삭제 권한 허용하면서 ResponseDTO 생성
        // 혹은 HTTP 요청을 보낸 사람의 권한이 ADMIN인 경우 => 수정, 삭제 권한 허용하면서 ResponseDTO 생성
        if (user.getUserId().equals(board.getUser().getUserId()) ||
                user.getAuthority().getField().equals("admin")) {

            BoardResponseDto.OneArticleResponseDto oneArticleResponseDto = BoardResponseDto.OneArticleResponseDto.builder()
                    .title(board.getTitle())
                    .category(board.getCategory())
                    .content(board.getContent())
                    .author(board.getUser().getNickname())
                    .canDelete(true)
                    .canUpdate(true)
                    .like(board.getLike())
                    .dislike(board.getDislike())
                    .comments(queryResult)
                    .createdAt(board.getCreatedAt())
                    .updatedAt(board.getUpdatedAt())
                    .build();
            return oneArticleResponseDto;
        }

        // 그 외에는 수정, 삭제 권한 허용하지 않으면서 ResponseDTO 생성
        BoardResponseDto.OneArticleResponseDto oneArticleResponseDto = BoardResponseDto.OneArticleResponseDto.builder()
                .title(board.getTitle())
                .category(board.getCategory())
                .content(board.getContent())
                .author(board.getUser().getNickname())
                .canDelete(false)
                .canUpdate(false)
                .like(board.getLike())
                .dislike(board.getDislike())
                .comments(queryResult)
                .createdAt(board.getCreatedAt())
                .updatedAt(board.getUpdatedAt())
                .build();
        return oneArticleResponseDto;
    }
}
