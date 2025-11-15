package kr.ac.hansung.cse.board_and_chatting.dto.jpa_dto.board_dto;

import kr.ac.hansung.cse.board_and_chatting.entity.User;
import kr.ac.hansung.cse.board_and_chatting.entity.enums.Category;

import java.time.LocalDateTime;

public interface BoardDto {
    Long getBoardId();
    String getTitle();
    String getContent();
    Category getCategory();
    String getAuthor(); // user.nickname 대신 alias 사용 가능
    Long getCommentCount();   // COUNT(c.id) AS commentCount
    Long getLike();
    Long getDislike();
    LocalDateTime getCreatedAt();
    LocalDateTime getUpdatedAt();
}
