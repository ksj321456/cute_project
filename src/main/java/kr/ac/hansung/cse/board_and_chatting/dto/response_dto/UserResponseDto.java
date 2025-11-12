package kr.ac.hansung.cse.board_and_chatting.dto.response_dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@AllArgsConstructor
@Builder
public class UserResponseDto {


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
}
