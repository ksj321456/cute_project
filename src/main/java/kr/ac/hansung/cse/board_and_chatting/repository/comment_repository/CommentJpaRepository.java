package kr.ac.hansung.cse.board_and_chatting.repository.comment_repository;

import kr.ac.hansung.cse.board_and_chatting.dto.jpa_dto.comment_dto.CommentCountWithOneArticleDto;
import kr.ac.hansung.cse.board_and_chatting.dto.jpa_dto.comment_dto.CommentDto;
import kr.ac.hansung.cse.board_and_chatting.dto.jpa_dto.comment_dto.CommentsInOneArticle;
import kr.ac.hansung.cse.board_and_chatting.entity.Comment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Slf4j
@RequiredArgsConstructor
public class CommentJpaRepository implements CommentRepository {

    private final JpaCommentRepository jpaCommentRepository;

    @Override
    public Comment save(Comment comment) {
        return jpaCommentRepository.save(comment);
    }

    @Override
    public List<CommentDto> findCommentCountCustom(List<Long> board_comment_id) {
        return jpaCommentRepository.findCommentCountCustom(board_comment_id);
    }

    @Override
    public List<CommentDto> findCommentByBoardIdCustom(Long boardId, Pageable pageable) {
        return jpaCommentRepository.findCommentByBoardIdCustom(boardId, pageable);
    }

    @Override
    public Page<Comment> findCommentByUserIdCustom(Long userId, Pageable pageable) {
        return jpaCommentRepository.findCommentByUserIdCustom(userId, pageable);
    }
}
