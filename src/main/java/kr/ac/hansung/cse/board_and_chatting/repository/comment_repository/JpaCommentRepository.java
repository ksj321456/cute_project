package kr.ac.hansung.cse.board_and_chatting.repository.comment_repository;

import kr.ac.hansung.cse.board_and_chatting.dto.jpa_dto.comment_dto.CommentCountWithOneArticleDto;
import kr.ac.hansung.cse.board_and_chatting.dto.jpa_dto.comment_dto.CommentDto;
import kr.ac.hansung.cse.board_and_chatting.dto.jpa_dto.comment_dto.CommentsInOneArticle;
import kr.ac.hansung.cse.board_and_chatting.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface JpaCommentRepository extends JpaRepository<Comment, Long> {

    @Query("SELECT new kr.ac.hansung.cse.board_and_chatting.dto.jpa_dto.comment_dto.CommentCountWithOneArticleDto(b.id, COUNT(c.id)) " +
            "FROM Board b LEFT JOIN b.comments c " +
            "WHERE b.id IN :boardIds " +
            "GROUP BY b.id " +
            "ORDER BY b.createdAt DESC")
    List<CommentDto> findCommentCountCustom(@Param("boardIds") List<Long> boardIds);

    @Query(value = "SELECT new kr.ac.hansung.cse.board_and_chatting.dto.jpa_dto.comment_dto.CommentsInOneArticle(" +
            "c.id, :boardId, c.content, u.nickname, c.createdAt, c.updatedAt) " +
            "FROM Comment c " +
            "JOIN c.user u " +
            "WHERE c.board.id = :boardId " +
            "ORDER BY c.createdAt DESC",
            countQuery = "SELECT COUNT(*) FROM Comment c WHERE c.board.id = :boardId")
    List<CommentDto> findCommentByBoardIdCustom(@Param("boardId") Long boardId, Pageable pageable);

    @Query(value = "SELECT c FROM Comment c WHERE c.user.id = :userId ORDER BY c.createdAt DESC",
    countQuery = "SELECT COUNT(c) FROM Comment c WHERE c.user.id = :userId")
    Page<Comment> findCommentByUserIdCustom(@Param("userId") Long userId, Pageable pageable);
}
