package kr.ac.hansung.cse.board_and_chatting.dto.response_dto;

import kr.ac.hansung.cse.board_and_chatting.entity.Board;
import kr.ac.hansung.cse.board_and_chatting.entity.Comment;
import kr.ac.hansung.cse.board_and_chatting.entity.Friend;
import lombok.*;

import java.util.List;

@Getter
@AllArgsConstructor
@Builder
public class UserResponseDto implements UserResponse {


    @Getter
    @AllArgsConstructor
    @Builder
    public static class SignUpResponseDto implements UserResponse {
        private String userId;

        private String password;

        private String nickname;
    }

    @Getter
    @AllArgsConstructor
    @Builder
    public static class LoginResponseDto implements UserResponse {
        private String userId;

        private String password;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ViewMyProfileResponseDto implements UserResponse {

        // 자기 닉네임
        private String nickname;

        // 자신이 작성한 게시글 전체 수
        private Long totalArticlePages;

        // 자신이 작성한 게시글들, 페이징 처리
        private List<String> articles;

        private Long totalCommentPages;

        // 자신이 작성한 댓글
        private List<String> comments;

        private Long totalFriendPages;

        // 자신의 친구들의 이름들
        private List<String> friendNames;
    }
}
